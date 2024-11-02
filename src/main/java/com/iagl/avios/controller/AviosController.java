package com.iagl.avios.controller;

import com.iagl.avios.model.AviosRequest;
import com.iagl.avios.model.AviosResponse;
import com.iagl.avios.service.AviosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/avios")
public class AviosController {

  private final AviosService aviosService;

  @Autowired
  public AviosController(AviosService aviosService) {
    this.aviosService = aviosService;
  }

  @PostMapping("/calculate")
  public ResponseEntity<AviosResponse> calculateAvios(
      @Valid @RequestBody AviosRequest aviosRequest) {

    AviosResponse response =
        aviosService.calculateAvios(
            aviosRequest.departureAirportCode(),
            aviosRequest.arrivalAirportCode(),
            aviosRequest.cabinCode());
    return ResponseEntity.ok(response);
  }
}
