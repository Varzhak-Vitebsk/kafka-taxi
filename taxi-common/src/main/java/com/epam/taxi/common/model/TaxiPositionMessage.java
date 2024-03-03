package com.epam.taxi.common.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TaxiPositionMessage(@NotBlank String taxiId, @NotBlank String latitude, @NotBlank String longitude) {

}
