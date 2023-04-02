package ru.papapers.optimalchoice.api.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class Purpose {

    private String name;

    private List<OptimizationCriterion> optimizationСriteria = new ArrayList<>();

    private Map<String, Double> purposeRelations = new HashMap<>();

}
