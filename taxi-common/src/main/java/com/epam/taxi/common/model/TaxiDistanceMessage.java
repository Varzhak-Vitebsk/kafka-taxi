package com.epam.taxi.common.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record TaxiDistanceMessage(@NotBlank String taxiId, @NotNull @PositiveOrZero Double distance) {

}
