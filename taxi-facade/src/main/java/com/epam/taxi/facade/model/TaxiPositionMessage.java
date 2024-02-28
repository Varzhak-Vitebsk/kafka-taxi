package com.epam.taxi.facade.model;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

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
