package ru.papapers.optimalchoice.domain.errors;

import ru.papapers.optimalchoice.domain.CriterionDto;

public class SubjectComputationError extends ComputationError {

    private CriterionDto criterionDto;

    public SubjectComputationError(String code, String message, CriterionDto criterionDto) {
        super(code, message);
    }
}
