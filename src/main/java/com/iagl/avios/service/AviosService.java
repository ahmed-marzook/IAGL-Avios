package com.iagl.avios.service;

import com.iagl.avios.model.AviosResponse;
import com.iagl.avios.enums.CabinCode;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class AviosService {
  private record AirportRoute(String departure, String arrival) {}

  private static final Map<AirportRoute, Integer> routeRewards = new HashMap<>();

  static {
    routeRewards.put(new AirportRoute("LHR", "LAX"), 4500);
    routeRewards.put(new AirportRoute("LHR", "SFO"), 4400);
    routeRewards.put(new AirportRoute("LHR", "JFK"), 3200);
    routeRewards.put(new AirportRoute("LGW", "YYZ"), 3250);
  }

  public AviosResponse calculateAvios(
      String departure, String arrival, Optional<CabinCode> cabinCode) {
    Objects.requireNonNull(departure, "Departure Airport has not been set");
    Objects.requireNonNull(arrival, "Arrival Airport has not been set");

    final Integer initialBaseAvios = getBaseAvios(departure, arrival);
    String cabinName = null;
    Map<String, Integer> options = new HashMap<>(Collections.emptyMap());
    Integer finalBaseAvios = initialBaseAvios;

    if (cabinCode.isEmpty()) {
      Arrays.stream(CabinCode.values())
          .forEach(cabin -> options.put(cabin.getName(), calculateBonus(initialBaseAvios, cabin)));
    } else {
      cabinName = cabinCode.get().getName();
      finalBaseAvios = calculateBonus(finalBaseAvios, cabinCode.get());
    }

    return new AviosResponse(departure, arrival, finalBaseAvios, cabinName, options);
  }

  private Integer getBaseAvios(String departure, String arrival) {
    return Optional.ofNullable(routeRewards.get(new AirportRoute(departure, arrival)))
        .orElseGet(
            () ->
                Optional.ofNullable(routeRewards.get(new AirportRoute(arrival, departure)))
                    .orElse(500));
  }

  private Integer calculateBonus(Integer baseAvios, CabinCode cabinCode) {
    return baseAvios + (baseAvios * cabinCode.getBonus() / 100);
  }
}
