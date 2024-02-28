package com.epam.taxi.facade.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaxiPositionRequest {

    String taxiId;
    String longitude;
    String latitude;
}
