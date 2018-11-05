package com.itrex.distance.measurer.service;

import com.itrex.distance.measurer.converter.CityDistanceConverter;
import com.itrex.distance.measurer.converter.RouteConverter;
import com.itrex.distance.measurer.event.model.RouteAdditionEvent;
import com.itrex.distance.measurer.exception.ResourceNotFoundException;
import com.itrex.distance.measurer.exception.ValidationException;
import com.itrex.distance.measurer.model.CityDistance;
import com.itrex.distance.measurer.model.Route;
import com.itrex.distance.measurer.model.dto.CityDistanceCreateTO;
import com.itrex.distance.measurer.model.dto.RouteTO;
import com.itrex.distance.measurer.persistence.CityDistanceRepository;
import com.itrex.distance.measurer.persistence.RouteRepository;
import com.itrex.distance.measurer.service.impl.CityDistanceServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CityDistanceServiceImplTest {

    private static final String CITY_A = "A";
    private static final String CITY_B = "B";
    private static final String CITY_C = "C";
    private static final String CITY_D = "D";
    private static final Long DISTANCE = 70L;
    private static final Long TOTAL_DISTANCE = 100L;

    @Mock
    private CityDistanceRepository cityDistanceRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private CityDistanceConverter cityDistanceConverter;

    @Mock
    private RouteConverter routeConverter;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private CityDistanceServiceImpl cityDistanceService;

    @Test(expected = ResourceNotFoundException.class)
    public void shouldReturnNotFoundException() {
        when(routeRepository.findAllRoutesBetweenCities(CITY_A, CITY_B)).thenReturn(Collections.emptyList());
        cityDistanceService.findAllRoutes(CITY_A, CITY_B);
    }

    @Test(expected = ValidationException.class)
    public void shouldReturnValidationExceptionForSameCitiesOnPost() {
        CityDistanceCreateTO createTO = new CityDistanceCreateTO();
        createTO.setCityFrom(CITY_A);
        createTO.setCityTo(CITY_A);
        cityDistanceService.addDistance(createTO);
    }

    @Test(expected = ValidationException.class)
    public void shouldReturnValidationExceptionForSameCitiesOnGet() {
        cityDistanceService.findAllRoutes(CITY_A, CITY_A);
    }

    @Test
    public void shouldAddNewDistance() {
        CityDistanceCreateTO createTO = createTO(CITY_A, CITY_B, DISTANCE);
        CityDistance mockedSavedDistance = Mockito.mock(CityDistance.class);
        when(cityDistanceRepository.findByCities(CITY_A, CITY_B)).thenReturn(Optional.empty());
        when(cityDistanceConverter.convertFrom(createTO)).thenReturn(mockedSavedDistance);
        when(mockedSavedDistance.getId()).thenReturn(1L);
        cityDistanceService.addDistance(createTO);
        verify(cityDistanceRepository).save(any(CityDistance.class));
        verify(applicationEventPublisher).publishEvent(any(RouteAdditionEvent.class));
        verify(cityDistanceConverter).convertFrom(any(CityDistance.class));
    }

    @Test
    public void shouldUpdateExistedDistance() {
        CityDistanceCreateTO createTO = createTO(CITY_A, CITY_B, DISTANCE);
        CityDistance mockedSavedDistance = Mockito.mock(CityDistance.class);
        when(cityDistanceRepository.findByCities(CITY_A, CITY_B)).thenReturn(Optional.of(mockedSavedDistance));
        when(mockedSavedDistance.getId()).thenReturn(1L);
        cityDistanceService.addDistance(createTO);
        verify(mockedSavedDistance).setDistance(DISTANCE);
        verify(cityDistanceRepository).save(any(CityDistance.class));
        verify(applicationEventPublisher).publishEvent(any(RouteAdditionEvent.class));
        verify(cityDistanceConverter).convertFrom(any(CityDistance.class));
    }

    @Test
    public void shouldReturnRouteInCorrectOrder() {
        Route route = createRoute(CITY_A, CITY_B);
        when(routeRepository.findAllRoutesBetweenCities(CITY_B, CITY_A)).thenReturn(singletonList(route));
        when(routeConverter.convertFrom(any(Route.class))).thenReturn(createRouteTO(route));
        List<RouteTO> allRoutes = cityDistanceService.findAllRoutes(CITY_B, CITY_A);
        Assert.assertEquals(allRoutes.get(0).getCities().get(0), CITY_B);
        Assert.assertEquals(allRoutes.get(0).getCities().get(allRoutes.get(0).getCities().size() - 1), CITY_A);
    }

    private RouteTO createRouteTO(Route route){
        RouteTO routeTO = new RouteTO();
        routeTO.setTotalDistance(route.getTotalDistance());
        routeTO.setCities(route.getPath());
        return routeTO;
    }

    private Route createRoute(String from, String to){
        Route route = new Route();
        route.setCityFrom(from);
        route.setCityTo(to);
        route.setTotalDistance(TOTAL_DISTANCE);
        route.setPath(asList(from, CITY_C, CITY_D, to));
        return route;
    }

    private CityDistanceCreateTO createTO(String from, String to, Long distance) {
        CityDistanceCreateTO createTO = new CityDistanceCreateTO();
        createTO.setCityFrom(from);
        createTO.setCityTo(to);
        createTO.setDistance(distance);
        return createTO;
    }
}
