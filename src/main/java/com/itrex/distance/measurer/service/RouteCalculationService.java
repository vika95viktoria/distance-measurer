package com.itrex.distance.measurer.service;

public interface RouteCalculationService {
    void updateRoutes(Long routeId);

    void recalculateWithNewDistance(Long routeId, Long difference);
}
