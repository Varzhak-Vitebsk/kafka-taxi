package com.epam.taxi.tracker.configuration;

import com.epam.taxi.common.model.TaxiDistanceMessage;
import com.epam.taxi.common.model.TaxiPositionMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class KafkaConfiguration {

  public static final String POSITION_RECEIVER_PROPERTIES = "taxiPositionKafkaReceiverProperties";
  public static final String DISTANCE_SENDER_PROPERTIES = "taxiDistanceKafkaSenderProperties";
  public static final String KAFKA_OBJECT_MAPPER = "kafkaObjectMapper";

  @Bean(KAFKA_OBJECT_MAPPER)
  public ObjectMapper kafkaObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    return objectMapper;
  }

  @Bean(DISTANCE_SENDER_PROPERTIES)
  @ConfigurationProperties(value = "kafka.producer.taxi-distance")
  public KafkaProducerProperties taxiDistanceKafkaSenderProperties() {
    return new KafkaProducerProperties();
  }

  @Bean(POSITION_RECEIVER_PROPERTIES)
  @ConfigurationProperties("kafka.consumer.taxi-position")
  public KafkaConsumerProperties taxiPositionKafkaReceiverProperties() {
    return new KafkaConsumerProperties();
  }

  @Bean
  public KafkaSender<String, TaxiDistanceMessage> taxiDistanceKafkaSender(
      @Qualifier(DISTANCE_SENDER_PROPERTIES) KafkaProducerProperties producerProperties,
      @Qualifier(KAFKA_OBJECT_MAPPER) ObjectMapper mapper) {
    SenderOptions<String, TaxiDistanceMessage> senderOptions = SenderOptions.<String, TaxiDistanceMessage>create(
            buildProducerConfigs(producerProperties)).maxInFlight(1024)
        .withKeySerializer(new StringSerializer())
        .withValueSerializer(new JsonSerializer<>(mapper));

    return KafkaSender.create(senderOptions);
  }

  private Map<String, Object> buildProducerConfigs(KafkaProducerProperties producerProperties) {

    Map<String, Object> clusterConfigs = new HashMap<>();
    clusterConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProperties.getBootstrapServers());
    Optional.ofNullable(producerProperties.getDeliveryTimeout())
        .map(deliverTimeout -> (int) deliverTimeout.toMillis())
        .ifPresent(deliveryTimeoutMillis ->
            clusterConfigs.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMillis)
        );

    return Stream.of(clusterConfigs)
        .flatMap(m -> m.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Bean
  public KafkaReceiver<String, TaxiPositionMessage> taxiPositionKafkaReceiver(
      @Qualifier(POSITION_RECEIVER_PROPERTIES) KafkaConsumerProperties producerProperties,
      @Qualifier(KAFKA_OBJECT_MAPPER) ObjectMapper mapper) {
    var jsonDeserializer = new JsonDeserializer<TaxiPositionMessage>(mapper);
    jsonDeserializer.addTrustedPackages("com.epam.taxi.*");

    ReceiverOptions<String, TaxiPositionMessage> receiverOptions = ReceiverOptions.<String, TaxiPositionMessage>create(
            buildConsumerConfigs(producerProperties))
        .withKeyDeserializer(new StringDeserializer())
        .withValueDeserializer(jsonDeserializer)
        .subscription(List.of(producerProperties.getTopic()));

    return KafkaReceiver.create(receiverOptions);
  }

  private Map<String, Object> buildConsumerConfigs(KafkaConsumerProperties consumerProperties) {
    Map<String, Object> clusterConfigs = Map.of(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getBootstrapServers(),
        ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getConsumerGroupId(),
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"
    );

    return Stream.of(clusterConfigs)
        .flatMap(m -> m.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

}
