package com.iagl.avios.model;

import com.iagl.avios.enums.CabinCode;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;

public record AviosRequest(
    @NotBlank(message = "Departure airport code is required") String departureAirportCode,
    @NotBlank(message = "Arrival airport code is required") String arrivalAirportCode,
    Optional<CabinCode> cabinCode) {}
