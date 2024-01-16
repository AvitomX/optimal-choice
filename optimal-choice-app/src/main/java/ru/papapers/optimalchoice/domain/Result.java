package ru.papapers.optimalchoice.domain;

import lombok.Builder;
import lombok.Data;
import ru.papapers.optimalchoice.model.Subject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class Result implements Serializable {

    private Map<Subject, BigDecimal> subjectPriorities;
    private final List<Object> errors;

}
