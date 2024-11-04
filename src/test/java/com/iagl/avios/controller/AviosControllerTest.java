package com.iagl.avios.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iagl.avios.enums.CabinCode;
import com.iagl.avios.model.AviosRequest;
import com.iagl.avios.model.AviosResponse;
import com.iagl.avios.service.AviosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AviosController.class)
public class AviosControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AviosService service;

  @Autowired private ObjectMapper objectMapper;

  private final AviosResponse mockAviosResponse =
      new AviosResponse("LHR", "JFK", 60000, CabinCode.M.getName(), null);

  @Test
  void calculateAvios_ValidRequest_ReturnsSuccessResponse() throws Exception {
    AviosRequest validRequest = new AviosRequest("LHR", "JFK", CabinCode.M);

    when(service.calculateAvios(anyString(), anyString(), any())).thenReturn(mockAviosResponse);

    mockMvc
        .perform(
            post("/api/v1/avios/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.departureAirportCode").value("LHR"))
        .andExpect(jsonPath("$.arrivalAirportCode").value("JFK"))
        .andExpect(jsonPath("$.aviosForCabin").value(60000))
        .andExpect(jsonPath("$.cabinClass").value("worldTraveller"))
        .andExpect(jsonPath("$.availableOptions").isEmpty());
  }

  @Test
  void calculateAvios_InvalidRequest_ReturnsGenericBadRequest() throws Exception {
    AviosRequest validRequest = new AviosRequest("LHR", "JFK", CabinCode.M);

    when(service.calculateAvios(anyString(), anyString(), any()))
        .thenThrow(new RuntimeException("Service error"));

    mockMvc
        .perform(
            post("/api/v1/avios/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.message").value("UNCAUGHT ERROR"))
        .andExpect(jsonPath("$.debugMessage").value("Service error"))
        .andExpect(jsonPath("$.fieldErrors").isEmpty())
        .andExpect(jsonPath("$.path").value("/api/v1/avios/calculate"));
  }

  @Test
  void whenMethodArgumentNotValid_thenReturnsValidationErrorDeparture() throws Exception {
    AviosRequest invalidRequest = new AviosRequest("", "GHF", null);

    mockMvc
        .perform(
            post("/api/v1/avios/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.message").value("Validation error"))
        .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
        .andExpect(jsonPath("$.fieldErrors[0].object").value("aviosRequest"))
        .andExpect(jsonPath("$.fieldErrors[0].field").value("departureAirportCode"))
        .andExpect(jsonPath("$.fieldErrors[0].rejectedValue").value(""))
        .andExpect(jsonPath("$.fieldErrors[0].message").value("Departure airport code is required"))
        .andExpect(jsonPath("$.path").value("/api/v1/avios/calculate"));
  }

  @Test
  void whenMethodArgumentNotValid_thenReturnsValidationErrorArrival() throws Exception {
    AviosRequest invalidRequest = new AviosRequest("GHF", "", null);

    mockMvc
        .perform(
            post("/api/v1/avios/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.message").value("Validation error"))
        .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
        .andExpect(jsonPath("$.fieldErrors[0].object").value("aviosRequest"))
        .andExpect(jsonPath("$.fieldErrors[0].field").value("arrivalAirportCode"))
        .andExpect(jsonPath("$.fieldErrors[0].rejectedValue").value(""))
        .andExpect(jsonPath("$.fieldErrors[0].message").value("Arrival airport code is required"))
        .andExpect(jsonPath("$.path").value("/api/v1/avios/calculate"));
  }

  @Test
  void calculateAvios_InvalidCabinCode_ReturnsBadRequest() throws Exception {
    String invalidRequestJson =
        """
            {
                "departureAirportCode": "LHR",
                "arrivalAirportCode": "JFK",
                "cabinCode": "INVALID"
            }
            """;

    mockMvc
        .perform(
            post("/api/v1/avios/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(
            jsonPath("$.message")
                .value(
                    "When provided, cabin code must be one of: M (World Traveller), W (World Traveller Plus), J (Club World), F (First)"))
        .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
        .andExpect(jsonPath("$.fieldErrors[0].object").value("aviosRequest"))
        .andExpect(jsonPath("$.fieldErrors[0].field").value("cabinCode"))
        .andExpect(jsonPath("$.fieldErrors[0].rejectedValue").value("INVALID"))
        .andExpect(
            jsonPath("$.fieldErrors[0].message").value("Cabin code must be one of M, W, J, F"))
        .andExpect(jsonPath("$.path").value("/api/v1/avios/calculate"));
  }
}
