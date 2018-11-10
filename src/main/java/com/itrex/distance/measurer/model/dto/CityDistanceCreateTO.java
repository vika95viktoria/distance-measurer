package com.itrex.distance.measurer.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class CityDistanceCreateTO {
    @NotNull
    private String cityFrom;
    @NotNull
    private String cityTo;
    @NotNull
    @Positive
    private Long distance;
}
