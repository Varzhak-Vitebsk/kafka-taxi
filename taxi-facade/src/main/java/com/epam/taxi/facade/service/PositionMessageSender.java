package com.epam.taxi.facade.service;

import com.epam.taxi.facade.configuration.KafkaProducerProperties;
import com.epam.taxi.facade.model.TaxiPositionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;

import static com.epam.taxi.facade.configuration.KafkaConfiguration.ORDER_SENDER_PROPERTIES;

@Service
@RequiredArgsConstructor
@Slf4j
public class PositionMessageSender {

  private final KafkaSender<String, TaxiPositionMessage> sender;
  @Qualifier(ORDER_SENDER_PROPERTIES)
  private final KafkaProducerProperties properties;

  public Mono<Void> sendMessage(TaxiPositionMessage message) {
    return sender.createOutbound().send(Mono.just(new ProducerRecord<>(
            properties.getTopic(),
            message.getTaxiId(),
            message)))
        .then()
        .doOnSuccess(v -> log.info("Message sent successfully to topic {} for order {}",
            properties.getTopic(),
            message.getTaxiId()))
        .doOnError(e -> log.error(
            "Something went wrong while sending a message to topic {} for order {}",
            properties.getTopic(),
            message.getTaxiId(), e));
  }
}
