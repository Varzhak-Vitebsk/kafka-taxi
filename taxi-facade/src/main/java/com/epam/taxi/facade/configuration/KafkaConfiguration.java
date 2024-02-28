package com.epam.taxi.facade.configuration;

import com.epam.taxi.facade.model.TaxiPositionMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class KafkaConfiguration {

  public static final String ORDER_SENDER_PROPERTIES = "orderKafkaSenderProperties";
  public static final String KAFKA_OBJECT_MAPPER = "kafkaObjectMapper";

  @Bean(KAFKA_OBJECT_MAPPER)
  public ObjectMapper kafkaObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    return objectMapper;
  }

  @Bean(ORDER_SENDER_PROPERTIES)
  @ConfigurationProperties(value = "kafka.producer.taxi")
  public KafkaProducerProperties orderKafkaSenderProperties() {
    return new KafkaProducerProperties();
  }

  @Bean
  public KafkaSender<String, TaxiPositionMessage> orderKafkaSender(
      @Qualifier(ORDER_SENDER_PROPERTIES) KafkaProducerProperties producerProperties,
      @Qualifier(KAFKA_OBJECT_MAPPER) ObjectMapper mapper) {
    SenderOptions<String, TaxiPositionMessage> senderOptions = SenderOptions.<String, TaxiPositionMessage>create(
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

}
