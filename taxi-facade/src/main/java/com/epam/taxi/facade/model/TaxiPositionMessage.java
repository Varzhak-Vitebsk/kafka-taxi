package com.epam.taxi.facade.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaxiPositionMessage {

  @NotBlank
  String taxiId;
  @NotBlank
  String latitude;
  @NotBlank
  String longitude;
}
