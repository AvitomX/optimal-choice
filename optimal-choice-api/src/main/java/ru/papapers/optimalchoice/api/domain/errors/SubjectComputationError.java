package ru.papapers.optimalchoice.api.domain.errors;


import ru.papapers.optimalchoice.api.domain.CriterionDto;

public class SubjectComputationError extends ComputationError {

    private CriterionDto criterionDto;

    public SubjectComputationError(String code, String message, CriterionDto criterionDto) {
        super(code, message);
    }
}
