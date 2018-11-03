package com.itrex.distance.measurer.event.listener;

import com.itrex.distance.measurer.event.model.RouteAdditionEvent;
import com.itrex.distance.measurer.service.RouteCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RouteAdditionEventListener {

    private RouteCalculationService routeCalculationService;

    @Autowired
    public RouteAdditionEventListener(RouteCalculationService routeCalculationService) {
        this.routeCalculationService = routeCalculationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleRouteAddition(RouteAdditionEvent event) {
        routeCalculationService.updateRoutes(event.getRouteId());
    }
}
