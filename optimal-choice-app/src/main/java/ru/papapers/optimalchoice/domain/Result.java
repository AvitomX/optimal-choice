package ru.papapers.optimalchoice.domain;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class Result implements Serializable {

    private final List<Object> errors;

}
