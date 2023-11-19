package ru.papapers.optimalchoice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.papapers.optimalchoice.model.Criterion;
import ru.papapers.optimalchoice.model.CriterionRelation;
import ru.papapers.optimalchoice.model.Estimation;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.repository.PurposeRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PurposeServiceTest {

    @Mock
    private PurposeRepository purposeRepository;
    @Mock
    private CriterionRelationService criterionRelationService;
    @Mock
    private SubjectRelationService subjectRelationService;

    @InjectMocks
    private PurposeService purposeService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void create() {
    }

    @Test
    void getOne() {
    }

    @Test
    void checkSuccessTest() {
        Purpose purpose = new Purpose();
        purpose.setId(UUID.randomUUID());
        purpose.setName("testName");

        Set<CriterionRelation> criterionRelations = new HashSet<>();

        criterionRelations.add(buildCriterionRelation(purpose, "name_1", "name_2", Estimation.TWO));
        criterionRelations.add(buildCriterionRelation(purpose, "name_1", "name_3", Estimation.THREE));
        criterionRelations.add(buildCriterionRelation(purpose, "name_1", "name_4", Estimation.FOUR));
        criterionRelations.add(buildCriterionRelation(purpose, "name_2", "name_3", Estimation.FIVE));
        criterionRelations.add(buildCriterionRelation(purpose, "name_2", "name_4", Estimation.SIX));
        criterionRelations.add(buildCriterionRelation(purpose, "name_3", "name_4", Estimation.SEVEN));

        purpose.setCriterionRelations(criterionRelations);
        purpose.setSubjectRelations(null);

        purposeService.check(purpose);

    }

    @Test
    void checkWhenThereIsNoOneCriterionRelationTest() {
        Purpose purpose = new Purpose();
        purpose.setId(UUID.randomUUID());
        purpose.setName("testName");

        Set<CriterionRelation> criterionRelations = new HashSet<>();

        criterionRelations.add(buildCriterionRelation(purpose, "name_1", "name_3", Estimation.THREE));
        criterionRelations.add(buildCriterionRelation(purpose, "name_1", "name_4", Estimation.FOUR));
        criterionRelations.add(buildCriterionRelation(purpose, "name_2", "name_3", Estimation.FIVE));
        criterionRelations.add(buildCriterionRelation(purpose, "name_2", "name_4", Estimation.SIX));
        criterionRelations.add(buildCriterionRelation(purpose, "name_3", "name_4", Estimation.SEVEN));

        purpose.setCriterionRelations(criterionRelations);
        purpose.setSubjectRelations(null);

        purposeService.check(purpose);

    }

    private static final Map<String, Criterion> CRITERION_MAP = new HashMap<>();

    private static CriterionRelation buildCriterionRelation(Purpose purpose,
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

    private static Criterion getCriterion(String criterionName) {
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