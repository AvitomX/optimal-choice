package ru.papapers.optimalchoice.api.domain.errors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.papapers.optimalchoice.api.domain.CriterionDto;
import ru.papapers.optimalchoice.api.domain.SubjectDto;


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
