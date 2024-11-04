package com.iagl.avios.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.iagl.avios.enums.CabinCode;
import com.iagl.avios.model.AviosResponse;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class AviosServiceTest {

  private AviosService service;

  @BeforeEach
  void setUp() {
    service = new AviosService();
  }

  static Stream<Arguments> routeBaseAvios() {
    return Stream.of(
        Arguments.of("LHR", "LAX", 4500),
        Arguments.of("LAX", "LHR", 4500),
        Arguments.of("LHR", "SFO", 4400),
        Arguments.of("SFO", "LHR", 4400),
        Arguments.of("LHR", "JFK", 3200),
        Arguments.of("JFK", "LHR", 3200),
        Arguments.of("LGW", "YYZ", 3250),
        Arguments.of("YYZ", "LGW", 3250),
        Arguments.of("LHR", "UNKNOWN", 500));
  }

  static Stream<Arguments> cabinBonusProvider() {
    return Stream.of(
        Arguments.of("LHR", "LAX", CabinCode.M, 4500),
        Arguments.of("LHR", "LAX", CabinCode.W, 5400),
        Arguments.of("LHR", "LAX", CabinCode.J, 6750),
        Arguments.of("LHR", "LAX", CabinCode.F, 9000),
        Arguments.of("XXX", "YYY", CabinCode.M, 500),
        Arguments.of("XXX", "YYY", CabinCode.W, 600),
        Arguments.of("XXX", "YYY", CabinCode.J, 750),
        Arguments.of("XXX", "YYY", CabinCode.F, 1000));
  }

  @ParameterizedTest
  @MethodSource("routeBaseAvios")
  void calculateAvios_ShouldReturnCorrectBaseAvios(
      String departure, String arrival, int expectedAvios) {
    AviosResponse response = service.calculateAvios(departure, arrival, null);

    assertThat(response)
        .isNotNull()
        .extracting(
            AviosResponse::aviosForCabin,
            AviosResponse::arrivalAirportCode,
            AviosResponse::departureAirportCode)
        .containsExactly(expectedAvios, arrival, departure);
  }

  @ParameterizedTest
  @MethodSource("cabinBonusProvider")
  void calculateAvios_WithCabinBonus_ShouldApplyCorrectBonus(
      String departure, String arrival, CabinCode cabinCode, int expectedAvios) {
    AviosResponse response = service.calculateAvios(departure, arrival, cabinCode);

    assertThat(response)
        .isNotNull()
        .extracting(
            AviosResponse::aviosForCabin,
            AviosResponse::arrivalAirportCode,
            AviosResponse::departureAirportCode)
        .containsExactly(expectedAvios, arrival, departure);
  }

  @Test
  void shouldReturnAllCabinOptionsWhenNoCabinSpecified() {
    AviosResponse response = service.calculateAvios("LHR", "LAX", null);

    assertThat(response)
        .isNotNull()
        .extracting(AviosResponse::availableOptions)
        .hasFieldOrPropertyWithValue("worldTraveller", 4500)
        .hasFieldOrPropertyWithValue("worldTravellerPlus", 5400)
        .hasFieldOrPropertyWithValue("clubWorld", 6750)
        .hasFieldOrPropertyWithValue("first", 9000);
  }

  @Test
  void calculateAvios_WithDepartureIsNull_ShouldThrowNull() {
    assertThatThrownBy(() -> service.calculateAvios(null, "LAX", null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Departure Airport has not been set");
  }

  @Test
  void calculateAvios_WithArrivalIsNull_ShouldThrowNull() {
    assertThatThrownBy(() -> service.calculateAvios("LHR", null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Arrival Airport has not been set");
  }
}
