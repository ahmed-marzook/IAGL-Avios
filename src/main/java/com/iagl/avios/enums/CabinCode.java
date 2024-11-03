package com.iagl.avios.enums;

import lombok.Getter;

@Getter
public enum CabinCode {
  M("worldTraveller", 0),
  W("worldTravellerPlus", 20),
  J("clubWorld", 50),
  F("first", 100);

  private final String name;
  private final int bonus;

  CabinCode(String cabinName, int bonus) {
    this.name = cabinName;
    this.bonus = bonus;
  }
}
