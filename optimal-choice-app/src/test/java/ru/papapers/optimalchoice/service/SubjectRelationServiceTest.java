package ru.papapers.optimalchoice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.papapers.optimalchoice.DataHelper;
import ru.papapers.optimalchoice.api.domain.Estimation;
import ru.papapers.optimalchoice.api.domain.errors.CriterionError;
import ru.papapers.optimalchoice.mapper.SubjectRelationMapper;
import ru.papapers.optimalchoice.model.*;
import ru.papapers.optimalchoice.repository.SubjectRelationRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.papapers.optimalchoice.api.domain.errors.ErrorCode.SUBJECT_COMPARING_ERROR;

@ExtendWith(MockitoExtension.class)
class SubjectRelationServiceTest {

    @Mock
    private SubjectRelationRepository repository;
    @Mock
    private CriterionService criterionService;
    @Mock
    private SubjectService subjectService;
    @Mock
    private SubjectRelationMapper mapper;

    @InjectMocks
    private SubjectRelationService subjectRelationService;

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

        Set<CriterionRelation> criterionRelations = new HashSet<>();
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, crit1, crit2, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, crit1, crit3, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, crit2, crit3, Estimation.FIVE));

        purpose.setCriterionRelations(criterionRelations);
    }

    @Test
    void successCheckTest() {
        Set<SubjectRelation> subjectRelations = new HashSet<>();
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit1, "sbj_1", "sbj_2", Estimation.NINE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit1, "sbj_1", "sbj_3", Estimation.EIGHT));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit1, "sbj_2", "sbj_3", Estimation.SEVEN));

        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit2, "sbj_1", "sbj_2", Estimation.FOUR));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit2, "sbj_1", "sbj_3", Estimation.TWO));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit2, "sbj_2", "sbj_3", Estimation.THREE));

        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit3, "sbj_1", "sbj_2", Estimation.EIGHT));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit3, "sbj_1", "sbj_3", Estimation.FIVE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit3, "sbj_2", "sbj_3", Estimation.EIGHT));

        purpose.setSubjectRelations(subjectRelations);
        Assertions.assertEquals(0, subjectRelationService.check(subjectRelations).size());
    }

    @Test
    void criterionFailureCheckTest() {
        CriterionError criterionError = new CriterionError("testCode", "testMsg", null);
        when(criterionService.createCriterionError(any(), any())).thenReturn(criterionError);

        Set<SubjectRelation> subjectRelations = new HashSet<>();
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit1, "sbj_1", "sbj_2", Estimation.NINE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit1, "sbj_1", "sbj_3", Estimation.EIGHT));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit1, "sbj_2", "sbj_3", Estimation.SEVEN));

        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit2, "sbj_1", "sbj_2", Estimation.FOUR));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit2, "sbj_1", "sbj_3", Estimation.TWO));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit2, "sbj_2", "sbj_3", Estimation.THREE));

        purpose.setSubjectRelations(subjectRelations);

        CriterionError error = (CriterionError) subjectRelationService.check(subjectRelations).get(0);
        Assertions.assertEquals(criterionError, error);
    }

    @Test
    void subjectFailureCheckTest() {
        Set<SubjectRelation> subjectRelations = new HashSet<>();
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit1, "sbj_1", "sbj_2", Estimation.NINE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit1, "sbj_1", "sbj_3", Estimation.EIGHT));

        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit2, "sbj_1", "sbj_2", Estimation.FOUR));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit2, "sbj_2", "sbj_3", Estimation.THREE));

        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit3, "sbj_1", "sbj_3", Estimation.FIVE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, crit3, "sbj_2", "sbj_3", Estimation.EIGHT));

        purpose.setSubjectRelations(subjectRelations);

        subjectRelationService.check(subjectRelations);

        verify(subjectService, times(6)).createSubjectError(any(), any(), eq(SUBJECT_COMPARING_ERROR));
    }
}