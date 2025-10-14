package com.project.CarGo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED"),
    IN_PROGRESS("IN PROGRESS"),
    PAID("PAID"),
    PENDING("PENDING");

    private final String displayName;
}
