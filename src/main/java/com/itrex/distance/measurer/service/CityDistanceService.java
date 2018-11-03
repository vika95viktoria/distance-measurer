package com.itrex.distance.measurer.service;

import com.itrex.distance.measurer.model.dto.CityDistanceCreateTO;
import com.itrex.distance.measurer.model.dto.CityDistanceTO;

public interface CityDistanceService {
    CityDistanceTO addDistance(CityDistanceCreateTO cityDistanceCreateTO);
}
