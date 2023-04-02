package ru.papapers.optimalchoice.api.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class OptimizationCriterion {

    private String name;

    private Map<String, Double> criterionRelations = new HashMap<>();
}
