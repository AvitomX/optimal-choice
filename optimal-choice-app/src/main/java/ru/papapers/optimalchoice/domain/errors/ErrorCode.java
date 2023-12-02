package ru.papapers.optimalchoice.domain.errors;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUBJECT_COMPARING_ERROR("4000", "Need to compare subject with every other one."),
    SUBJECT_RELATIONS_ABSENCE_ERROR("4100", "There are no subject relations for this criterion."),
    CRITERION_COMPARING_ERROR("4101", "Need to compare criterion with every other one.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
