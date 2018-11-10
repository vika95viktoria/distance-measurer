package com.itrex.distance.measurer.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RouteUpdateEvent {
    private Long routeId;
    private Long difference;
}
