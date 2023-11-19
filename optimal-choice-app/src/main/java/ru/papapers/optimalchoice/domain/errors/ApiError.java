package ru.papapers.optimalchoice.domain.errors;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
@Builder
public class ApiError {

    private Serializable errorObject;

    private String message;

}
