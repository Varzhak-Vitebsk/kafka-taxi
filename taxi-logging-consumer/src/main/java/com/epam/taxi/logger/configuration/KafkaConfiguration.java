package com.epam.taxi.logger.configuration;

import com.epam.taxi.common.model.TaxiDistanceMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class KafkaConfiguration {

  public static final String DISTANCE_RECEIVER_PROPERTIES = "taxiDistanceKafkaReceiverProperties";
  public static final String KAFKA_OBJECT_MAPPER = "kafkaObjectMapper";

  @Bean(KAFKA_OBJECT_MAPPER)
  public ObjectMapper kafkaObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    return objectMapper;
  }

  @Bean(DISTANCE_RECEIVER_PROPERTIES)
  @ConfigurationProperties("kafka.consumer.taxi-distance")
  public KafkaConsumerProperties taxiPositionKafkaReceiverProperties() {
    return new KafkaConsumerProperties();
  }

  @Bean
  public KafkaReceiver<String, TaxiDistanceMessage> taxiPositionKafkaReceiver(
      @Qualifier(DISTANCE_RECEIVER_PROPERTIES) KafkaConsumerProperties producerProperties,
      @Qualifier(KAFKA_OBJECT_MAPPER) ObjectMapper mapper) {
    var jsonDeserializer = new JsonDeserializer<TaxiDistanceMessage>(mapper);
    jsonDeserializer.addTrustedPackages("com.epam.taxi.*");

    ReceiverOptions<String, TaxiDistanceMessage> receiverOptions = ReceiverOptions.<String, TaxiDistanceMessage>create(
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