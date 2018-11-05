package com.itrex.distance.measurer.service;

import com.itrex.distance.measurer.model.CityDistance;
import com.itrex.distance.measurer.model.Route;
import com.itrex.distance.measurer.persistence.CityDistanceRepository;
import com.itrex.distance.measurer.persistence.RouteRepository;
import com.itrex.distance.measurer.service.impl.RouteCalculationServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RouteCalculationServiceImplTest {

    private static final Long CITY_DISTANCE_ID = 1L;
    private static final Long B_C_DISTANCE = 10L;
    private static final Long A_B_DISTANCE = 5L;
    private static final Long D_C_DISTANCE = 15L;
    private static final String CITY_A = "A";
    private static final String CITY_B = "B";
    private static final String CITY_C = "C";
    private static final String CITY_D = "D";
    @Mock
    private CityDistanceRepository cityDistanceRepository;
    @Mock
    private RouteRepository routeRepository;
    @InjectMocks
    private RouteCalculationServiceImpl routeCalculationService;
    @Captor
    private ArgumentCaptor<Route> routeArgumentCaptor;
    @Captor
    private ArgumentCaptor<List<Route>> bRouteArgumentCaptor;

    /**
     * Service has already had an information between two connections between A and B
     * and between C and D. This test will check the service behaviour after addition
     * of B - C connection. Service should create 4 new routes: B-C, A-C, A-D, B-D
     */

    @Test
    public void shouldCreateAllNewRoutes() {
        when(cityDistanceRepository.getOne(CITY_DISTANCE_ID)).thenReturn(createCityDistance());
        Route abRoute = createDirectRoute(CITY_A, CITY_B, A_B_DISTANCE);
        Route cdRoute = createDirectRoute(CITY_C, CITY_D, D_C_DISTANCE);
        when(routeRepository.findAllRoutesToCity(CITY_B)).thenReturn(Collections.singletonList(abRoute));
        when(routeRepository.findAllRoutesToCity(CITY_C)).thenReturn(Collections.singletonList(cdRoute));
        routeCalculationService.updateRoutes(CITY_DISTANCE_ID);
        verify(routeRepository).save(routeArgumentCaptor.capture());
        checkDirectRoute(routeArgumentCaptor.getValue());
        verify(routeRepository, times(3)).saveAll(bRouteArgumentCaptor.capture());

        List<Route> bRoutes = bRouteArgumentCaptor.getAllValues().get(0);
        assertEquals(bRoutes.size(), 1);
        assertTrue(bRoutes.get(0).getTotalDistance() == A_B_DISTANCE + B_C_DISTANCE);
        assertEquals(bRoutes.get(0).getCityFrom(), CITY_A);
        assertEquals(bRoutes.get(0).getCityTo(), CITY_C);
        assertEquals(bRoutes.get(0).getPath(), Arrays.asList(CITY_A, CITY_B, CITY_C));

        List<Route> cRoutes = bRouteArgumentCaptor.getAllValues().get(1);
        assertEquals(cRoutes.size(), 1);
        assertTrue(cRoutes.get(0).getTotalDistance() == D_C_DISTANCE + B_C_DISTANCE);
        assertEquals(cRoutes.get(0).getCityFrom(), CITY_B);
        assertEquals(cRoutes.get(0).getCityTo(), CITY_D);
        assertEquals(cRoutes.get(0).getPath(), Arrays.asList(CITY_B, CITY_C, CITY_D));

        List<Route> combinedRoutes = bRouteArgumentCaptor.getAllValues().get(2);
        assertEquals(combinedRoutes.size(), 1);
        assertTrue(combinedRoutes.get(0).getTotalDistance() == D_C_DISTANCE + B_C_DISTANCE + A_B_DISTANCE);
        assertEquals(combinedRoutes.get(0).getCityFrom(), CITY_A);
        assertEquals(combinedRoutes.get(0).getCityTo(), CITY_D);
        assertEquals(combinedRoutes.get(0).getPath(), Arrays.asList(CITY_A, CITY_B, CITY_C, CITY_D));
    }

    private void checkDirectRoute(Route directRoute) {
        assertEquals(directRoute.getCityFrom(), CITY_B);
        assertEquals(directRoute.getCityTo(), CITY_C);
        assertEquals(directRoute.getTotalDistance(), B_C_DISTANCE);
        assertEquals(directRoute.getPath().get(0), CITY_B);
        assertEquals(directRoute.getPath().get(1), CITY_C);
    }

    private CityDistance createCityDistance() {
        CityDistance cityDistance = new CityDistance();
        cityDistance.setCityFrom(CITY_B);
        cityDistance.setCityTo(CITY_C);
        cityDistance.setDistance(B_C_DISTANCE);
        return cityDistance;
    }

    private Route createDirectRoute(String from, String to, Long distance) {
        Route route = new Route();
        route.setCityFrom(from);
        route.setCityTo(to);
        route.setTotalDistance(distance);
        route.setPath(Arrays.asList(from, to));
        return route;
    }
}
