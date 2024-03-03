package com.epam.taxi.logger.service;

import com.epam.taxi.common.model.TaxiDistanceMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

@Component
@RequiredArgsConstructor
@Slf4j
public class DistanceMessageReceiver {

  private final KafkaReceiver<String, TaxiDistanceMessage> receiver;

  public Flux<TaxiDistanceMessage> receiveTaxiDistance() {
    return receiver.receive()
        .map(record -> {
          log.info("Message received successfully from topic {} for taxi {}", record.topic(), record.key());
          record.receiverOffset().acknowledge();
          return record.value();
        })
        .onErrorContinue((throwable, record) -> {
          log.error("Failed to read message: {}", throwable.getMessage());
          ((ReceiverRecord<String,TaxiDistanceMessage>) record).receiverOffset().acknowledge();
        });
  }

}