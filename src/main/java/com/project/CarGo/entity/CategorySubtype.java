package com.project.CarGo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategorySubtype {
    COMPACT("Compact"),
    ECONOMY("Economy"),
    FULL_SIZE("Full Size"),
    INTERMEDIATE("Intermediate"),
    LUXURY("Luxury"),
    MID_SIZE("Mid size"),
    PREMIUM("Premium"),
    STANDARD("Standard");

    private final String displayName;
}
