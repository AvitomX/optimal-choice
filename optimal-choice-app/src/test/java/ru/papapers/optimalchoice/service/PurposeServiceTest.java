package ru.papapers.optimalchoice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.papapers.optimalchoice.model.CriterionRelation;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.model.SubjectRelation;
import ru.papapers.optimalchoice.repository.PurposeRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    void checkTest() {
        Purpose purpose = new Purpose();
        purpose.setId(UUID.randomUUID());
        purpose.setName("testName");

        Set<CriterionRelation> criterionRelations = new HashSet<>();
        purpose.setCriterionRelations(criterionRelations);

        Set<SubjectRelation> subjectRelations = new HashSet<>();
        purpose.setSubjectRelations(subjectRelations);

        purposeService.check(purpose);

        verify(criterionRelationService, times(1)).check(criterionRelations);
        verify(subjectRelationService, times(1)).check(subjectRelations);
    }
}