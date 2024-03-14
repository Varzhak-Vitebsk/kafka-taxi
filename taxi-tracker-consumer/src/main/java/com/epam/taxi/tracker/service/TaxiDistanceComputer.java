package com.epam.taxi.tracker.service;

import com.epam.taxi.common.model.TaxiDistanceMessage;
import com.epam.taxi.tracker.model.TaxiCoordinates;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class TaxiDistanceComputer {

  private final PositionMessageReceiver receiver;
  private final DistanceMessageSender sender;
  //TODO on application restart all data will be lost, need to sink of some storage
  private ConcurrentMap<String, TaxiCoordinates> coordinatesByTaxi = new ConcurrentHashMap<>();

  @EventListener(ApplicationStartedEvent.class)
  public void computeDistance() {
    receiver.receiveTaxiPosition()
        .<TaxiDistanceMessage>handle((taxiPositionMessage, sink) -> {
          if (coordinatesByTaxi.containsKey(taxiPositionMessage.taxiId())) {
            var coordinates = coordinatesByTaxi.get(taxiPositionMessage.taxiId());
            coordinates.setPreviousLatitude(coordinates.getCurrentLatitude());
            coordinates.setPreviousLongitude(coordinates.getCurrentLongitude());
            coordinates.setCurrentLatitude(Double.parseDouble(taxiPositionMessage.latitude()));
            coordinates.setCurrentLongitude(Double.parseDouble(taxiPositionMessage.longitude()));
            sink.next(TaxiDistanceMessage.builder()
                .taxiId(taxiPositionMessage.taxiId())
                .distance(computeDistance(coordinates))
                .build());
          } else {
            coordinatesByTaxi.putIfAbsent(taxiPositionMessage.taxiId(), TaxiCoordinates.builder()
                .currentLatitude(Double.parseDouble(taxiPositionMessage.latitude()))
                .currentLongitude(Double.parseDouble(taxiPositionMessage.longitude()))
                .previousLatitude(0D)
                .currentLongitude(0D)
                .build());
          }
        })
        .flatMap(sender::sendMessage)
        .subscribe();
  }

  //TODO for more precise calculation use formulae for distance on globe
  private double computeDistance(TaxiCoordinates message) {
    return Math.sqrt(Math.pow(message.getCurrentLatitude() - message.getPreviousLatitude(), 2)
        + Math.pow(message.getCurrentLongitude() - message.getPreviousLongitude(), 2));
  }

}
