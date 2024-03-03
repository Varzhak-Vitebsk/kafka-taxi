package com.epam.taxi.tracker.service;

import com.epam.taxi.common.model.TaxiDistanceMessage;
import com.epam.taxi.tracker.configuration.KafkaProducerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;

import static com.epam.taxi.tracker.configuration.KafkaConfiguration.DISTANCE_SENDER_PROPERTIES;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistanceMessageSender {

  private final KafkaSender<String, TaxiDistanceMessage> sender;
  @Qualifier(DISTANCE_SENDER_PROPERTIES)
  private final KafkaProducerProperties properties;

  public Mono<Void> sendMessage(TaxiDistanceMessage message) {
    return sender.createOutbound().send(Mono.just(new ProducerRecord<>(
            properties.getTopic(),
            message.taxiId(),
            message)))
        .then()
        .doOnSuccess(v -> log.info("Message sent successfully to topic {} for taxi {}",
            properties.getTopic(),
            message.taxiId()))
        .doOnError(e -> log.error(
            "Something went wrong while sending a message to topic {} for taxi {}",
            properties.getTopic(),
            message.taxiId(), e));
  }
}
