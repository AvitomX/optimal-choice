package ru.papapers.optimalchoice.api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CriterionRelationDto {

    private UUID id;

    private CriterionDto criterion;

    private CriterionDto comparingCriterion;

    private String value;
}
