package com.itrex.distance.measurer.service.impl;

import com.itrex.distance.measurer.model.CityDistance;
import com.itrex.distance.measurer.model.Route;
import com.itrex.distance.measurer.persistence.CityDistanceRepository;
import com.itrex.distance.measurer.persistence.RouteRepository;
import com.itrex.distance.measurer.service.RouteCalculationService;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteCalculationServiceImpl implements RouteCalculationService {

    private CityDistanceRepository cityDistanceRepository;
    private RouteRepository routeRepository;

    @Autowired
    public RouteCalculationServiceImpl(CityDistanceRepository cityDistanceRepository,
                                       RouteRepository routeRepository) {
        this.cityDistanceRepository = cityDistanceRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    @Transactional
    public void updateRoutes(Long routeId) {
        CityDistance newRoute = cityDistanceRepository.getOne(routeId);
        List<Route> routesToFrom = routeRepository.findAllRoutesToCity(newRoute.getCityFrom())
                .stream()
                .filter(route -> !route.getPath().contains(newRoute.getCityTo()))
                .collect(Collectors.toList());
        routeRepository.saveAll(createNewRoutes(newRoute, newRoute.getCityFrom(), routesToFrom));
        List<Route> routesTo = routeRepository.findAllRoutesToCity(newRoute.getCityTo()).stream()
                .filter(route -> !route.getPath().contains(newRoute.getCityFrom()))
                .collect(Collectors.toList());
        routeRepository.saveAll(createNewRoutes(newRoute, newRoute.getCityTo(), routesTo));
        routeRepository.save(createDirectRoute(newRoute));
    }

    private Route createDirectRoute(CityDistance cityDistance) {
        Route route = new Route();
        route.setCityFrom(cityDistance.getCityFrom());
        route.setCityTo(cityDistance.getCityTo());
        route.setTotalDistance(cityDistance.getDistance());
        route.setPath(Arrays.asList(cityDistance.getCityFrom(), cityDistance.getCityTo()));
        return route;
    }

    private List<Route> createNewRoutes(CityDistance route, String city, List<Route> routes) {
        String newCity = route.getCityFrom().equals(city) ? route.getCityTo() : route.getCityFrom();
        return routes.stream()
                .map(oldRoute -> {
                    Route newRoute = new Route();
                    List<String> cities = new ArrayList<>(oldRoute.getPath());
                    String secondCity;
                    if (oldRoute.getCityFrom().equals(city)) {
                        secondCity = cities.get(cities.size() - 1);
                        cities.add(0, newCity);
                    } else {
                        secondCity = cities.get(0);
                        cities.add(newCity);
                    }
                    newRoute.setPath(cities);
                    List<String> endOfPath = Arrays.asList(secondCity, newCity);
                    Collections.sort(endOfPath);
                    newRoute.setCityFrom(endOfPath.get(0));
                    newRoute.setCityTo(endOfPath.get(1));
                    newRoute.setTotalDistance(oldRoute.getTotalDistance() + route.getDistance());
                    return newRoute;
                })
                .collect(Collectors.toList());
    }
}
