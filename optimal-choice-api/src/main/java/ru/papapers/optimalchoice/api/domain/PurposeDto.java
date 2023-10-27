package ru.papapers.optimalchoice.api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PurposeDto {

    private UUID id;

    private String name;

    private Set<CriterionRelationDto> criterionRelations;
}
