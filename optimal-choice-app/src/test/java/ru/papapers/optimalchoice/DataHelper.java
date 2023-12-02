package ru.papapers.optimalchoice;

import ru.papapers.optimalchoice.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DataHelper {
    private final Map<String, Criterion> CRITERION_MAP = new HashMap<>();
    private final Map<String, Subject> SUBJECT_MAP = new HashMap<>();

    public SubjectRelation buildSubjectRelation(Purpose purpose,
                                                       Criterion criterion,
                                                       String subjectName,
                                                       String comparingSubjectName,
                                                       Estimation estimation) {
        SubjectRelation subjectRelation = new SubjectRelation();
        subjectRelation.setPurpose(purpose);
        subjectRelation.setCriterion(criterion);
        subjectRelation.setSubject(getSubject(subjectName));
        subjectRelation.setComparingSubject(getSubject(comparingSubjectName));
        subjectRelation.setEstimation(estimation);
        subjectRelation.setId(UUID.randomUUID());

        return subjectRelation;
    }

    public Subject getSubject(String name) {
        Subject subjectFromMap = SUBJECT_MAP.get(name);

        if (subjectFromMap == null) {
            Subject subject = new Subject();
            subject.setId(UUID.randomUUID());
            subject.setName(name);

            SUBJECT_MAP.put(name, subject);

            return subject;
        } else {
            return subjectFromMap;
        }
    }

    public CriterionRelation buildCriterionRelation(Purpose purpose,
                                                           String criterionName,
                                                           String comparingCriterionName,
                                                           Estimation estimation) {
        CriterionRelation criterionRelation = new CriterionRelation();

        criterionRelation.setId(UUID.randomUUID());
        criterionRelation.setPurpose(purpose);

        criterionRelation.setCriterion(getCriterion(criterionName));
        criterionRelation.setComparingCriterion(getCriterion(comparingCriterionName));

        criterionRelation.setEstimation(estimation);

        return criterionRelation;
    }

    public CriterionRelation buildCriterionRelation(Purpose purpose,
                                                    Criterion criterion,
                                                    Criterion comparingCriterion,
                                                    Estimation estimation) {
        CriterionRelation criterionRelation = new CriterionRelation();

        criterionRelation.setId(UUID.randomUUID());
        criterionRelation.setPurpose(purpose);

        criterionRelation.setCriterion(getCriterion(criterion.getName()));
        criterionRelation.setComparingCriterion(getCriterion(comparingCriterion.getName()));

        criterionRelation.setEstimation(estimation);

        return criterionRelation;
    }

    public Criterion getCriterion(String criterionName) {
        Criterion criterionFromMap = CRITERION_MAP.get(criterionName);

        if (criterionFromMap == null) {
            Criterion criterion = new Criterion();
            criterion.setId(UUID.randomUUID());
            criterion.setName(criterionName);

            CRITERION_MAP.put(criterionName, criterion);

            return criterion;
        } else {
            return criterionFromMap;
        }
    }
}
