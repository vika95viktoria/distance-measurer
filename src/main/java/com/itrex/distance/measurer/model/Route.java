package com.itrex.distance.measurer.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cityFrom;
    private String cityTo;
    private Long totalDistance;
    @ElementCollection
    private List<String> path = new ArrayList<>();
}
