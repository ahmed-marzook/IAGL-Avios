package com.iagl.avios.enums;

import lombok.Getter;

@Getter
public enum CabinCode {
  M("World Traveller", 0),
  W("World Traveller Plus", 20),
  J("Club World", 50),
  F("First", 100);

  private final String name;
  private final int bonus;

  CabinCode(String cabinName, int bonus) {
    this.name = cabinName;
    this.bonus = bonus;
  }
}
