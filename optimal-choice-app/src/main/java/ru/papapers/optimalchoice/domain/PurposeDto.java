package ru.papapers.optimalchoice.domain;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class PurposeDto {

    private UUID id;

    private String name;

    private Set<CriterionRelationDto> criterionRelations;

    private Set<SubjectRelationDto> subjectRelations;
}
