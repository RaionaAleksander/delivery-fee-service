package com.example.deliveryfeeservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum City {
    TALLINN("Tallinn-Harku"),
    TARTU("Tartu-Tõravere"),
    PARNU("Pärnu");

    /** Name of the weather station for this city */
    private final String stationName;
}