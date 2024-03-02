package com.epam.taxi.facade.contoller;

import com.epam.taxi.facade.model.TaxiPositionMessage;
import com.epam.taxi.facade.model.TaxiPositionRequest;
import com.epam.taxi.facade.service.PositionMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TaxiControllerV1Impl implements TaxiControllerV1 {

    private final PositionMessageSender sender;

    @Override
    public Mono<ResponseEntity<Void>> addTaxiPosition(TaxiPositionRequest request) {
        log.debug("Received position for taxi: %s".formatted(request.getTaxiId()));
        return sender.sendMessage(TaxiPositionMessage.builder()
                        .taxiId(request.getTaxiId())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .build())
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
    }
}
