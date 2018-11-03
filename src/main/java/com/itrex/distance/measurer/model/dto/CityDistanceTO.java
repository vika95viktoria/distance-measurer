package com.itrex.distance.measurer.model.dto;

import lombok.Data;

@Data
public class CityDistanceTO {
    private Long id;
    private String cityFrom;
    private String cityTo;
    private Long distance;
}
