package ru.papapers.optimalchoice.api.domain.errors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.papapers.optimalchoice.api.domain.CriterionDto;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CriterionError extends BaseError {

    private CriterionDto criterion;

    public CriterionError(String code, String message, CriterionDto criterion) {
        super(code, message);
        this.criterion = criterion;
    }
}
