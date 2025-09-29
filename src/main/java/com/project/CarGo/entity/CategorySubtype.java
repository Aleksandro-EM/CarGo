package com.project.CarGo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategorySubtype {
    COMPACT("Compact"),
    ECONOMY("Economy"),
    LUXURY("Luxury"),
    INTERMEDIATE("Intermediate"),
    FULL_SIZE("Full Size"),
    MID_SIZE("Mid size"),
    PREMIUM("Premium"),
    STANDARD("Standard");

    private final String displayName;
}
