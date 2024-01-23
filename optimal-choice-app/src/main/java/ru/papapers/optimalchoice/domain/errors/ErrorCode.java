package ru.papapers.optimalchoice.domain.errors;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUBJECT_COMPARING_ERROR("4001", "Need to compare subject with every other one."),
    SUBJECT_COUNT_ERROR("4002", "There must be two subjects at least."),

    CRITERION_SUBJECT_RELATIONS_ABSENCE_ERROR("4100", "There are no subject relations for this criterion."),
    CRITERION_COMPARING_ERROR("4101", "Need to compare criterion with every other one."),
    CRITERION_COUNT_ERROR("4102", "There must be two criteria at least."),

    CRITERIA_CONSISTENCY_ERROR("4500", "Need to review criteria comparing."),
    SUBJECT_CONSISTENCY_ERROR("4501", "Need to review subject comparing.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
