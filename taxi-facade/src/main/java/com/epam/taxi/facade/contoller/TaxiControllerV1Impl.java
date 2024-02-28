package com.epam.taxi.facade.contoller;

import com.epam.taxi.facade.model.TaxiPositionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaxiControllerV1Impl implements TaxiControllerV1 {

    @Override
    public ResponseEntity<Void> addTaxiPosition(TaxiPositionRequest request) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
