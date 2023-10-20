package ru.papapers.optimalchoice.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class CriterionRelationDto {

    private UUID id;

    private CriterionDto criterion;

    private CriterionDto comparingCriterion;

    private String value;
}
