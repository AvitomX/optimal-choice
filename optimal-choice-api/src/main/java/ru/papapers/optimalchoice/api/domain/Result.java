package ru.papapers.optimalchoice.api.domain;

import lombok.Builder;
import lombok.Data;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class Result implements Serializable {

    private Map<SubjectDto, BigDecimal> subjectPriorities;
    private final List<Object> errors;

}
