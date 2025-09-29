package com.project.CarGo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryType {
    SUV("SUV"),
    CAR("Car"),
    VAN("Van"),
    PICKUP("Pickup"),
    ELECTRIC("Electric"),
    HYBRID("Hybrid");

    private final String displayName;
}
