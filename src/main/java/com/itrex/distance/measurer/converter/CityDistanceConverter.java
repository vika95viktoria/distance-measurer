package com.itrex.distance.measurer.converter;

import com.itrex.distance.measurer.model.CityDistance;
import com.itrex.distance.measurer.model.dto.CityDistanceCreateTO;
import com.itrex.distance.measurer.model.dto.CityDistanceTO;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class CityDistanceConverter {

    public CityDistanceTO convertFrom(CityDistance cityDistance) {
        CityDistanceTO cityDistanceTO = new CityDistanceTO();
        cityDistanceTO.setId(cityDistance.getId());
        cityDistanceTO.setCityFrom(cityDistance.getCityFrom());
        cityDistanceTO.setCityTo(cityDistance.getCityTo());
        cityDistanceTO.setDistance(cityDistance.getDistance());
        return cityDistanceTO;
    }

    public CityDistance convertFrom(CityDistanceCreateTO cityDistanceCreateTO) {
        List<String> cities = Arrays.asList(cityDistanceCreateTO.getCityFrom(), cityDistanceCreateTO.getCityTo());
        Collections.sort(cities);
        CityDistance cityDistance = new CityDistance();
        cityDistance.setCityFrom(cities.get(0));
        cityDistance.setCityTo(cities.get(1));
        cityDistance.setDistance(cityDistanceCreateTO.getDistance());
        return cityDistance;
    }
}
