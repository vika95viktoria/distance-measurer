package com.itrex.distance.measurer.converter;

import com.itrex.distance.measurer.model.Route;
import com.itrex.distance.measurer.model.dto.RouteTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RouteConverter {
    public RouteTO convertFrom(Route route) {
        RouteTO routeTO = new RouteTO();
        List<String> cities = route.getPath();
        routeTO.setCities(cities);
        routeTO.setTotalDistance(route.getTotalDistance());
        return routeTO;
    }
}
