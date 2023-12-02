package ru.papapers.optimalchoice.domain.errors;

import lombok.*;
import ru.papapers.optimalchoice.domain.CriterionDto;
import ru.papapers.optimalchoice.domain.SubjectDto;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubjectError extends BaseError {

    private SubjectDto subject;

    private CriterionDto criterion;

    public SubjectError(String code, String message, SubjectDto subject, CriterionDto criterion) {
        super(code, message);
        this.subject = subject;
        this.criterion = criterion;
    }
}
