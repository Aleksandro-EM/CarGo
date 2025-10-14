package com.project.CarGo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryType {
    CAR("Car"),
    ELECTRIC("Electric"),
    HYBRID("Hybrid"),
    PICKUP("Pickup"),
    SUV("SUV"),
    VAN("Van");

    private final String displayName;
}
