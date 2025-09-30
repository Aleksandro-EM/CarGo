package com.project.CarGo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    PENDING("PENDING"),
    PAID("PAID"),
    IN_PROGRESS("IN PROGRESS"),
    COMPLETED("COMPLETED");

    private final String displayName;
}
