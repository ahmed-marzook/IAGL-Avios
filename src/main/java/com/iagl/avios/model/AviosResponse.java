package com.iagl.avios.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AviosResponse {
  private final String departureAirportCode;
  private final String arrivalAirportCode;
  private final Integer aviosForCabin;
  private final String cabinClass;
  private final Map<String, Integer> availableOptions;
}
