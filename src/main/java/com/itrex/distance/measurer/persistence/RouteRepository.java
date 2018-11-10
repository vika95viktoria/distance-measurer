package com.itrex.distance.measurer.persistence;

import com.itrex.distance.measurer.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    @Query("SELECT r FROM Route r WHERE r.cityFrom = :city OR r.cityTo = :city")
    List<Route> findAllRoutesToCity(@Param("city") String city);

    @Query("SELECT r FROM Route r WHERE (r.cityFrom = :from AND r.cityTo = :to) OR (r.cityTo = :from AND r.cityFrom = :to)")
    List<Route> findAllRoutesBetweenCities(@Param("from") String cityFrom, @Param("to") String cityTo);

    @Query(nativeQuery = true, value = "SELECT ROUTE_ID FROM ROUTE_PATH GROUP BY ROUTE_ID " +
            "HAVING ARRAY_CONTAINS(ARRAY_AGG(PATH), ?1) AND ARRAY_CONTAINS(ARRAY_AGG(PATH), ?2)")
    Set<BigInteger> findIdsOfRoutesContainBothCities(String cityFrom, String cityTo);
}
