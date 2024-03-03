package com.epam.taxi.logger.service;

import com.epam.taxi.common.model.TaxiDistanceMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxiDistanceLogger {

  private final DistanceMessageReceiver receiver;

  @EventListener(ApplicationStartedEvent.class)
  public void computeDistance() {
    receiver.receiveTaxiDistance()
        .<TaxiDistanceMessage>handle((taxiDistanceMessage, sink) -> {
          log.info("Taxi {} has travelled distance of {}", taxiDistanceMessage.taxiId(), taxiDistanceMessage.distance());
            sink.next(taxiDistanceMessage);
        })
        .subscribe();
  }

}