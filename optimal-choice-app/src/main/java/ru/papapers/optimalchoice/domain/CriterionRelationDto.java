package ru.papapers.optimalchoice.domain;

import lombok.Data;
import ru.papapers.optimalchoice.model.Estimation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CriterionRelationDto {

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
