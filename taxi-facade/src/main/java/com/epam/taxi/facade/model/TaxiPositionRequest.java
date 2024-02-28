package com.epam.taxi.facade.model;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@Builder
public class TaxiPositionRequest {

    @NotBlank
    String taxiId;
    @NotBlank
    String longitude;
    @NotBlank
    String latitude;
}
