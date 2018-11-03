package com.itrex.distance.measurer.service;

import com.itrex.distance.measurer.model.dto.CityDistanceCreateTO;
import com.itrex.distance.measurer.model.dto.CityDistanceTO;
import com.itrex.distance.measurer.model.dto.RouteTO;

import java.util.List;

public interface CityDistanceService {
    CityDistanceTO addDistance(CityDistanceCreateTO cityDistanceCreateTO);

    List<RouteTO> findAllRoutes(String cityFrom, String cityTo);
}
