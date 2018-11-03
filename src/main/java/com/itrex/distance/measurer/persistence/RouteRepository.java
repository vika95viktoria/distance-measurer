package com.itrex.distance.measurer.persistence;

import com.itrex.distance.measurer.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    @Query("SELECT r FROM Route r WHERE r.cityFrom = :city OR r.cityTo = :city")
    List<Route> findAllRoutesToCity(@Param("city") String city);

    @Query("SELECT r FROM Route r WHERE (r.cityFrom = :from AND r.cityTo = :to) OR (r.cityTo = :from AND r.cityFrom = :to)")
    List<Route> findAllRoutesBetweenCities(@Param("from") String cityFrom, @Param("to") String cityTo);
}
