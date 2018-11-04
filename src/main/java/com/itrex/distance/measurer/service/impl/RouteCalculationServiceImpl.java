package com.itrex.distance.measurer.service.impl;

import com.itrex.distance.measurer.model.CityDistance;
import com.itrex.distance.measurer.model.Route;
import com.itrex.distance.measurer.persistence.CityDistanceRepository;
import com.itrex.distance.measurer.persistence.RouteRepository;
import com.itrex.distance.measurer.service.RouteCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
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
        routeRepository.save(createDirectRoute(newRoute));
        List<Route> routesToFirst = routeRepository.findAllRoutesToCity(newRoute.getCityFrom())
                .stream()
                .filter(route -> !route.getPath().contains(newRoute.getCityTo()))
                .collect(Collectors.toList());
        routeRepository.saveAll(createNewRoutes(newRoute, newRoute.getCityFrom(), routesToFirst));
        List<Route> routesToSecond = routeRepository.findAllRoutesToCity(newRoute.getCityTo()).stream()
                .filter(route -> !route.getPath().contains(newRoute.getCityFrom()))
                .collect(Collectors.toList());
        routeRepository.saveAll(createNewRoutes(newRoute, newRoute.getCityTo(), routesToSecond));
        routeRepository.saveAll(calculateAllRoutes(routesToFirst, routesToSecond, newRoute.getCityFrom(), newRoute.getCityTo()));
    }

    private List<Route> createNewRoutes(CityDistance route, String city, List<Route> routes) {
        String newCity = route.getCityFrom().equals(city) ? route.getCityTo() : route.getCityFrom();
        return routes.stream()
                .map(oldRoute -> {
                    List<String> cities = new ArrayList<>(oldRoute.getPath());
                    String secondCity;
                    if (cities.get(0).equals(city)) {
                        secondCity = cities.get(cities.size() - 1);
                        cities.add(0, newCity);
                    } else {
                        secondCity = cities.get(0);
                        cities.add(newCity);
                    }
                    List<String> endOfPath = Arrays.asList(secondCity, newCity);
                    Collections.sort(endOfPath);
                    return createRoute(cities, endOfPath, oldRoute.getTotalDistance() + route.getDistance());
                })
                .collect(Collectors.toList());
    }

    private List<Route> calculateAllRoutes(List<Route> routesToFirst, List<Route> routesToSecond,
                                           String cityFrom, String cityTo) {
        List<Route> combinedRoutes = new ArrayList<>();
        for(Route routeA : routesToFirst) {
            for(Route routeB: routesToSecond) {
                if(!cityFrom.equals(routeA.getPath().get(routeA.getPath().size() - 1))) {
                    Collections.reverse(routeA.getPath());
                }
                if(!cityTo.equals(routeB.getPath().get(0))) {
                    Collections.reverse(routeB.getPath());
                }
                List<String> cities = new ArrayList<>(routeA.getPath());
                cities.addAll(routeB.getPath());
                List<String> endsOfPath = Arrays.asList(cities.get(0), cities.get(cities.size() - 1));
                Collections.sort(endsOfPath);
                boolean hasLoops = cities.stream()
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet().stream()
                        .anyMatch(entry -> entry.getValue() > 1);
                if(!hasLoops) {
                    combinedRoutes.add(createRoute(
                            cities, endsOfPath, routeA.getTotalDistance() + routeB.getTotalDistance()));
                }
            }
        }
        return combinedRoutes;
    }

    private Route createDirectRoute(CityDistance cityDistance) {
        Route route = new Route();
        route.setCityFrom(cityDistance.getCityFrom());
        route.setCityTo(cityDistance.getCityTo());
        route.setTotalDistance(cityDistance.getDistance());
        route.setPath(Arrays.asList(cityDistance.getCityFrom(), cityDistance.getCityTo()));
        return route;
    }

    private Route createRoute(List<String> cities, List<String> endsOfPath, Long distance) {
        Route route = new Route();
        route.setPath(cities);
        route.setCityFrom(endsOfPath.get(0));
        route.setCityTo(endsOfPath.get(1));
        route.setTotalDistance(distance);
        return route;
    }
}
