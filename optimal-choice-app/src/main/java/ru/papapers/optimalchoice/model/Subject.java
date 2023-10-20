package ru.papapers.optimalchoice.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Subject {

    private String name;

    private List<Criterion> criteria = new ArrayList<>();

    public boolean addCriterion(Criterion criterion) {
        return criteria.add(criterion);
    }

}
