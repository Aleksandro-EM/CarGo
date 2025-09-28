package com.project.CarGo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum CategoryType {
    SUV(new String[]{"COMPACT", "ECONOMY", "LUXURY", "INTERMEDIATE", "FULL SIZE", "MID SIZE", "PREMIUM", "STANDARD"}),
    CAR(new String[]{"COMPACT", "ECONOMY", "LUXURY", "INTERMEDIATE", "FULL SIZE", "MID SIZE", "PREMIUM", "STANDARD"}),
    VAN(new String[]{"ECONOMY", "INTERMEDIATE", "STANDARD"}),
    PICKUP(new String[]{"ECONOMY", "INTERMEDIATE", "STANDARD", "FULL SIZE"}),
    ELECTRIC(new String[]{"COMPACT", "ECONOMY", "INTERMEDIATE", "FULL SIZE", "MID SIZE", "STANDARD"}),
    HYBRID(new String[]{"COMPACT", "ECONOMY", "INTERMEDIATE", "FULL SIZE", "MID SIZE", "STANDARD"});

    private final String[] subtypes;

    public boolean isSubtype(String subtype) {
        return Arrays.asList(subtypes).contains(subtype);
    }
}
