package com.epam.taxi.tracker.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxiCoordinates {

  private Double previousLatitude;
  private Double previousLongitude;
  private Double currentLatitude;
  private Double currentLongitude;

}
