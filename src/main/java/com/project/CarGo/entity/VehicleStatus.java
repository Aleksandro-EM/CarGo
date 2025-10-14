package com.project.CarGo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VehicleStatus {
    AVAILABLE,
    MAINTENANCE,
    RENTED,
    RESERVED,
    UNAVAILABLE;
}
