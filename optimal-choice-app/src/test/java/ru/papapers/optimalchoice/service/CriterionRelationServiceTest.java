package ru.papapers.optimalchoice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.papapers.optimalchoice.DataHelper;
import ru.papapers.optimalchoice.api.domain.Estimation;
import ru.papapers.optimalchoice.mapper.CriterionRelationMapper;
import ru.papapers.optimalchoice.model.Criterion;
import ru.papapers.optimalchoice.model.CriterionRelation;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.repository.CriterionRelationRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.papapers.optimalchoice.api.domain.errors.ErrorCode.CRITERION_COMPARING_ERROR;

@ExtendWith(MockitoExtension.class)
class CriterionRelationServiceTest {

    @Mock
    private CriterionRelationRepository criterionRelationRepository;
    @Mock
    private CriterionService criterionService;
    @Mock
    private CriterionRelationMapper mapper;

    @InjectMocks
    private CriterionRelationService criterionRelationService;

    private DataHelper dataHelper;
    private Purpose purpose;
    private Criterion crit1;
    private Criterion crit2;
    private Criterion crit3;

    @BeforeEach
    void setUp() {
        dataHelper = new DataHelper();

        purpose = new Purpose();
        purpose.setId(UUID.randomUUID());
        purpose.setName("testName");

        crit1 = dataHelper.getCriterion("crit_1");
        crit2 = dataHelper.getCriterion("crit_2");
        crit3 = dataHelper.getCriterion("crit_3");
    }

    @Test
    void addTest() {
    }

    @Test
    void addOneTest() {
    }

    @Test
    void checkSuccessTest() {
        Set<CriterionRelation> criterionRelations = new HashSet<>();
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, crit1, crit2, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, crit1, crit3, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, crit2, crit3, Estimation.FIVE));

        assertEquals(0, criterionRelationService.check(criterionRelations).size());
    }

    @Test
    void checkFailureTest() {
        Set<CriterionRelation> criterionRelations = new HashSet<>();
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, crit1, crit2, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, crit2, crit3, Estimation.FIVE));

        criterionRelationService.check(criterionRelations);

        verify(criterionService, times(2)).createCriterionError(any(), eq(CRITERION_COMPARING_ERROR));
    }
}