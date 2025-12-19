package ru.papapers.optimalchoice.domain.math;

import lombok.Getter;
import ru.papapers.optimalchoice.model.Criterion;
import ru.papapers.optimalchoice.model.Subject;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Vector {

    private final Criterion criterion;
    private final MathContext context;
    private final Map<Subject, MathContext> subjectContextMap = new HashMap<>();

    public Vector(Criterion criterion, int totalCriterionCount) {
        this.criterion = criterion;
        this.context = new MathContext(totalCriterionCount);
    }
    public MathContext addSubjectContext(Subject subject, MathContext context) {
        return subjectContextMap.put(subject, context);
    }

}
