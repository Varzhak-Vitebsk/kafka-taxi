package com.epam.taxi.facade.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;


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
