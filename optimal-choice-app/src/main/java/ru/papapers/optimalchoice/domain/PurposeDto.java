package ru.papapers.optimalchoice.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

@Data
public class PurposeDto {

    private UUID id;

    @NotBlank
    private String name;

    private Set<CriterionRelationDto> criterionRelations;

    private Set<SubjectRelationDto> subjectRelations;
}
