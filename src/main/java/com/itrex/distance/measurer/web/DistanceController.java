package com.itrex.distance.measurer.web;

import com.itrex.distance.measurer.model.dto.CityDistanceCreateTO;
import com.itrex.distance.measurer.model.dto.CityDistanceTO;
import com.itrex.distance.measurer.model.dto.RouteTO;
import com.itrex.distance.measurer.service.CityDistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DistanceController {

    private CityDistanceService cityDistanceService;

    @Autowired
    public DistanceController(CityDistanceService cityDistanceService) {
        this.cityDistanceService = cityDistanceService;
    }

    @PostMapping("/route")
    public CityDistanceTO addDistance(@RequestBody CityDistanceCreateTO cityDistanceCreateTO) {
        return cityDistanceService.addDistance(cityDistanceCreateTO);
    }

    @GetMapping("/routes")
    public List<RouteTO> getRoutes(@RequestParam String from, @RequestParam String to) {
        return null;
    }
}
