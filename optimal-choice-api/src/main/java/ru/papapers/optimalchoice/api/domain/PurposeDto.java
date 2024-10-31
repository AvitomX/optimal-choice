package ru.papapers.optimalchoice.api.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Data
public class PurposeDto implements Serializable {

    private UUID id;

    @NotBlank
    private String name;

    private Set<CriterionRelationDto> criterionRelations;

    private Set<SubjectRelationDto> subjectRelations;
}
