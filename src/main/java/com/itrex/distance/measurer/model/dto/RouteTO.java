package com.itrex.distance.measurer.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class RouteTO {
    private List<String> cities;
    private Long totalDistance;
}
