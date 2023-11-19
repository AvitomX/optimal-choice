package ru.papapers.optimalchoice.domain;

import lombok.Builder;
import lombok.Data;
import ru.papapers.optimalchoice.domain.violations.Violation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Result implements Serializable {

    private final List<Violation> violations = new ArrayList<>();

}
