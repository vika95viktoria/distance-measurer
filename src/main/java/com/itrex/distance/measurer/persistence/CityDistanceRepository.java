package com.itrex.distance.measurer.persistence;

import com.itrex.distance.measurer.model.CityDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityDistanceRepository extends JpaRepository<CityDistance, Long> {
    @Query("SELECT ct FROM CityDistance ct WHERE ct.cityFrom = :from AND ct.cityTo = :to")
    Optional<CityDistance> findByCities(@Param("from") String from, @Param("to") String to);
}
