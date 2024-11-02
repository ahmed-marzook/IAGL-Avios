package com.iagl.avios.service;

import com.iagl.avios.enums.CabinCode;
import com.iagl.avios.model.AviosResponse;
import java.util.Arrays;
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

    return cabinCode
        .map(cabin -> buildSingleCabinResponse(initialBaseAvios, cabin))
        .orElseGet(() -> buildAllCabinsResponse(initialBaseAvios));
  }

  private AviosResponse buildSingleCabinResponse(int baseAvios, CabinCode cabin) {
    return AviosResponse.builder()
        .aviosForCabin(calculateBonus(baseAvios, cabin))
        .cabinClass(cabin.getName())
        .build();
  }

  private AviosResponse buildAllCabinsResponse(int baseAvios) {
    Map<String, Integer> options =
        Arrays.stream(CabinCode.values())
            .collect(
                Collectors.toMap(CabinCode::getName, cabin -> calculateBonus(baseAvios, cabin)));

    return AviosResponse.builder().aviosForCabin(baseAvios).availableOptions(options).build();
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