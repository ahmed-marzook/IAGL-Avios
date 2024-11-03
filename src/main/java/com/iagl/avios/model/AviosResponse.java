package com.iagl.avios.model;

import java.util.Map;

public record AviosResponse(
    String departureAirportCode,
    String arrivalAirportCode,
    Integer aviosForCabin,
    String cabinClass,
    Map<String, Integer> availableOptions) {}
