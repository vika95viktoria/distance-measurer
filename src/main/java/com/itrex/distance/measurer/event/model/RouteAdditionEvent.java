package com.itrex.distance.measurer.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RouteAdditionEvent {
    private Long routeId;
}
