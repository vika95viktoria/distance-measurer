package com.itrex.distance.measurer.service.impl;

import com.itrex.distance.measurer.converter.CityDistanceConverter;
import com.itrex.distance.measurer.converter.RouteConverter;
import com.itrex.distance.measurer.event.model.RouteAdditionEvent;
import com.itrex.distance.measurer.event.model.RouteUpdateEvent;
import com.itrex.distance.measurer.exception.ResourceNotFoundException;
import com.itrex.distance.measurer.exception.ValidationException;
import com.itrex.distance.measurer.model.CityDistance;
import com.itrex.distance.measurer.model.Route;
import com.itrex.distance.measurer.model.dto.CityDistanceCreateTO;
import com.itrex.distance.measurer.model.dto.CityDistanceTO;
import com.itrex.distance.measurer.model.dto.RouteTO;
import com.itrex.distance.measurer.persistence.CityDistanceRepository;
import com.itrex.distance.measurer.persistence.RouteRepository;
import com.itrex.distance.measurer.service.CityDistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CityDistanceServiceImpl implements CityDistanceService {

    private CityDistanceRepository cityDistanceRepository;
    private CityDistanceConverter cityDistanceConverter;
    private ApplicationEventPublisher eventPublisher;
    private RouteRepository routeRepository;
    private RouteConverter routeConverter;

    private static final String CITY_EQUALITY_ERROR = "City from can't be equal to city to";
    private static final String NO_CONNECTION_ERROR = "No route available";

    @Autowired
    public CityDistanceServiceImpl(CityDistanceRepository cityDistanceRepository,
                                   CityDistanceConverter cityDistanceConverter,
                                   ApplicationEventPublisher eventPublisher,
                                   RouteRepository routeRepository,
                                   RouteConverter routeConverter) {
        this.cityDistanceRepository = cityDistanceRepository;
        this.cityDistanceConverter = cityDistanceConverter;
        this.eventPublisher = eventPublisher;
        this.routeRepository = routeRepository;
        this.routeConverter = routeConverter;
    }

    @Override
    @Transactional
    public CityDistanceTO addDistance(CityDistanceCreateTO cityDistanceCreateTO) {
        if (cityDistanceCreateTO.getCityFrom().equals(cityDistanceCreateTO.getCityTo())) {
            throw new ValidationException(CITY_EQUALITY_ERROR);
        }
        List<String> cities = Arrays.asList(cityDistanceCreateTO.getCityFrom(), cityDistanceCreateTO.getCityTo());
        Collections.sort(cities);
        Optional<CityDistance> mayBeDistance = cityDistanceRepository.findByCities(cities.get(0), cities.get(1));
        CityDistance cityDistance;
        if (mayBeDistance.isPresent()) {
            cityDistance = mayBeDistance.get();
            if(!cityDistanceCreateTO.getDistance().equals(cityDistance.getDistance())) {
                Long difference = cityDistanceCreateTO.getDistance() - cityDistance.getDistance();
                cityDistance.setDistance(cityDistanceCreateTO.getDistance());
                cityDistanceRepository.save(cityDistance);
                eventPublisher.publishEvent(new RouteUpdateEvent(cityDistance.getId(), difference));
            }
        } else {
            cityDistance = cityDistanceConverter.convertFrom(cityDistanceCreateTO);
            cityDistanceRepository.save(cityDistance);
            eventPublisher.publishEvent(new RouteAdditionEvent(cityDistance.getId()));
        }
        return cityDistanceConverter.convertFrom(cityDistance);
    }

    @Override
    public List<RouteTO> findAllRoutes(String cityFrom, String cityTo) {
        if (cityFrom.equals(cityTo)) {
            throw new ValidationException(CITY_EQUALITY_ERROR);
        }
        List<Route> routes = routeRepository.findAllRoutesBetweenCities(cityFrom, cityTo);
        if (routes.isEmpty()) {
            throw new ResourceNotFoundException(NO_CONNECTION_ERROR + " from " + cityFrom + " to " + cityTo);
        }
        return routes.stream()
                .map(routeConverter::convertFrom)
                .peek(routeTO -> {
                    if (!routeTO.getCities().get(0).equals(cityFrom)) {
                        Collections.reverse(routeTO.getCities());
                    }
                })
                .collect(Collectors.toList());
    }
}
