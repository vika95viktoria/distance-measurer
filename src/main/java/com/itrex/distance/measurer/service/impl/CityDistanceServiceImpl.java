package com.itrex.distance.measurer.service.impl;

import com.itrex.distance.measurer.converter.CityDistanceConverter;
import com.itrex.distance.measurer.exception.ValidationException;
import com.itrex.distance.measurer.model.CityDistance;
import com.itrex.distance.measurer.model.dto.CityDistanceCreateTO;
import com.itrex.distance.measurer.model.dto.CityDistanceTO;
import com.itrex.distance.measurer.persistence.CityDistanceRepository;
import com.itrex.distance.measurer.service.CityDistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class CityDistanceServiceImpl implements CityDistanceService {

    private CityDistanceRepository cityDistanceRepository;
    private CityDistanceConverter cityDistanceConverter;

    @Autowired
    public CityDistanceServiceImpl(CityDistanceRepository cityDistanceRepository,
                                   CityDistanceConverter cityDistanceConverter) {
        this.cityDistanceRepository = cityDistanceRepository;
        this.cityDistanceConverter = cityDistanceConverter;
    }

    @Override
    @Transactional
    public CityDistanceTO addDistance(CityDistanceCreateTO cityDistanceCreateTO) {
        if(cityDistanceCreateTO.getCityFrom().equals(cityDistanceCreateTO.getCityTo())) {
            throw new ValidationException("City from can't be equal to city to");
        }
        List<String> cities = Arrays.asList(cityDistanceCreateTO.getCityFrom(), cityDistanceCreateTO.getCityTo());
        Collections.sort(cities);
        Optional<CityDistance> mayBeDistance = cityDistanceRepository.findByCities(cities.get(0), cities.get(1));
        CityDistance cityDistance;
        if(mayBeDistance.isPresent()) {
            cityDistance = mayBeDistance.get();
            cityDistance.setDistance(cityDistanceCreateTO.getDistance());

        } else {
            cityDistance = cityDistanceConverter.convertFrom(cityDistanceCreateTO);
        }
        cityDistanceRepository.save(cityDistance);
        return cityDistanceConverter.convertFrom(cityDistance);
    }
}
