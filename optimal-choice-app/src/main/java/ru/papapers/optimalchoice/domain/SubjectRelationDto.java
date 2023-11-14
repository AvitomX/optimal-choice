package ru.papapers.optimalchoice.domain;

import lombok.Data;
import ru.papapers.optimalchoice.model.Estimation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class SubjectRelationDto {

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
