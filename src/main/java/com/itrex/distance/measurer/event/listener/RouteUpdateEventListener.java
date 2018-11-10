package com.itrex.distance.measurer.event.listener;

import com.itrex.distance.measurer.event.model.RouteAdditionEvent;
import com.itrex.distance.measurer.event.model.RouteUpdateEvent;
import com.itrex.distance.measurer.service.RouteCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RouteUpdateEventListener {
    private RouteCalculationService routeCalculationService;

    @Autowired
    public RouteUpdateEventListener(RouteCalculationService routeCalculationService) {
        this.routeCalculationService = routeCalculationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleRouteDistanceUpdate(RouteUpdateEvent routeUpdateEvent) {
        routeCalculationService.recalculateWithNewDistance(routeUpdateEvent.getRouteId(), routeUpdateEvent.getDifference());
    }
}
