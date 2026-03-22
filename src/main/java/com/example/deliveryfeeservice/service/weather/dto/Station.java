package com.example.deliveryfeeservice.service.weather.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class Station {

    @JacksonXmlProperty(localName = "name")
    private String name;

    @JacksonXmlProperty(localName = "airtemperature")
    private Double temperature;

    @JacksonXmlProperty(localName = "windspeed")
    private Double windSpeed;

    @JacksonXmlProperty(localName = "phenomenon")
    private String phenomenon;
}