package com.epam.taxi.tracker.service;

import com.epam.taxi.common.model.TaxiPositionMessage;
import com.epam.taxi.tracker.configuration.KafkaConsumerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import static com.epam.taxi.tracker.configuration.KafkaConfiguration.POSITION_RECEIVER_PROPERTIES;

@Component
@RequiredArgsConstructor
@Slf4j
public class PositionMessageReceiver {

  private final KafkaReceiver<String, TaxiPositionMessage> receiver;
  @Qualifier(POSITION_RECEIVER_PROPERTIES)
  private final KafkaConsumerProperties properties;

  public Flux<TaxiPositionMessage> receiveTaxiPosition() {
    return receiver.receive()
        .map(record -> {
          log.info("Message received successfully from topic {} for taxi {}", record.topic(), record.key());
          record.receiverOffset().acknowledge();
          return record.value();
        })
        .onErrorContinue((throwable, record) -> {
          log.error("Failed to read message: {}", throwable.getMessage());
          ((ReceiverRecord<String,TaxiPositionMessage>) record).receiverOffset().acknowledge();
        });
  }

}
