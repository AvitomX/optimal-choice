package ru.papapers.optimalchoice.api.domain;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
public class CriterionRelationDto implements Serializable {

    private UUID id;

    @Valid
    @NotNull
    private CriterionDto criterion;

    @Valid
    @NotNull
    private CriterionDto comparingCriterion;

    @NotNull
    private Estimation estimation;
}
