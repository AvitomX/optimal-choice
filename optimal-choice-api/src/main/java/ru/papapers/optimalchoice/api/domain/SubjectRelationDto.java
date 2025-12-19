package ru.papapers.optimalchoice.api.domain;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
public class SubjectRelationDto implements Serializable {

    private UUID id;

    @Valid
    @NotNull
    private CriterionDto criterion;

    @Valid
    @NotNull
    private SubjectDto subject;

    @Valid
    @NotNull
    private SubjectDto comparingSubject;

    @NotNull
    private Estimation estimation;

}
