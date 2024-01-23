package ru.papapers.optimalchoice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.papapers.optimalchoice.DataHelper;
import ru.papapers.optimalchoice.domain.Result;
import ru.papapers.optimalchoice.mapper.CriterionMapperImpl;
import ru.papapers.optimalchoice.model.*;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @Spy
    private PurposeService purposeService = new PurposeService(null, null, null);

    @Spy
    private CriterionMapperImpl criterionMapper;

    @InjectMocks
    private ResultService resultService;

    private Purpose purpose;
    private DataHelper dataHelper;
    private Criterion size;
    private Criterion publicTransport;
    private Criterion district;
    private Criterion year;
    private Criterion neighborhood;
    private Criterion equipment;
    private Criterion commonCondition;
    private Criterion price;
    private Subject a;
    private Subject b;
    private Subject c;


    @BeforeEach
    void setUp() {
        dataHelper = new DataHelper();

        purpose = new Purpose();
        purpose.setId(UUID.randomUUID());
        purpose.setName("House");

        size = dataHelper.getCriterion("size");
        publicTransport = dataHelper.getCriterion("public transport");
        district = dataHelper.getCriterion("district");
        year = dataHelper.getCriterion("year");
        neighborhood = dataHelper.getCriterion("neighborhood");
        equipment = dataHelper.getCriterion("equipment");
        commonCondition = dataHelper.getCriterion("common condition");
        price = dataHelper.getCriterion("price");

        Set<CriterionRelation> criterionRelations = new HashSet<>();
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, size, publicTransport, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, size, district, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, size, year, Estimation.SEVEN));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, size, neighborhood, Estimation.SIX));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, size, equipment, Estimation.SIX));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, commonCondition, size, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, price, size, Estimation.FOUR));

        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, district, publicTransport, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, publicTransport, year, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, publicTransport, neighborhood, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, publicTransport, equipment, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, commonCondition, publicTransport, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, price, publicTransport, Estimation.SEVEN));

        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, district, year, Estimation.SIX));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, district, neighborhood, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, district, equipment, Estimation.FOUR));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, district, commonCondition, Estimation.SIX));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, price, district, Estimation.FIVE));

        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, neighborhood, year, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, equipment, year, Estimation.FOUR));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, commonCondition, year, Estimation.SEVEN));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, price, year, Estimation.EIGHT));

        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, equipment, neighborhood, Estimation.TWO));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, commonCondition, neighborhood, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, price, neighborhood, Estimation.SIX));

        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, commonCondition, equipment, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, price, equipment, Estimation.SIX));

        criterionRelations.add(dataHelper.buildCriterionRelation(purpose, price, commonCondition, Estimation.TWO));

        purpose.setCriterionRelations(criterionRelations);


        a = dataHelper.getSubject("A");
        b = dataHelper.getSubject("B");
        c = dataHelper.getSubject("C");

        Set<SubjectRelation> subjectRelations = new HashSet<>();
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, size, a, b, Estimation.SIX));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, size, a, c, Estimation.EIGHT));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, size, b, c, Estimation.FOUR));

        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, publicTransport, a, b, Estimation.SEVEN));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, publicTransport, c, a, Estimation.FIVE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, publicTransport, c, b, Estimation.EIGHT));

        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, district, a, b, Estimation.EIGHT));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, district, a, c, Estimation.SIX));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, district, c, b, Estimation.FOUR));

        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, year, a, b, Estimation.ONE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, year, a, c, Estimation.ONE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, year, b, c, Estimation.ONE));

        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, neighborhood, a, b, Estimation.FIVE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, neighborhood, a, c, Estimation.FOUR));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, neighborhood, c, b, Estimation.THREE));

        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, equipment, a, b, Estimation.EIGHT));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, equipment, a, c, Estimation.SIX));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, equipment, c, b, Estimation.FIVE));

        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, commonCondition, b, a, Estimation.TWO));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, commonCondition, c, a, Estimation.TWO));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, commonCondition, b, c, Estimation.ONE));

        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, price, b, a, Estimation.SEVEN));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, price, c, a, Estimation.FIVE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose, price, b, c, Estimation.THREE));

        purpose.setSubjectRelations(subjectRelations);
    }

    @Test
    void computeTest() {
        doReturn(purpose).when(purposeService).getOne(any());
        doReturn(new ArrayList<>()).when(purposeService).check(any());

        Result result = resultService.compute(UUID.randomUUID());

        Map<Subject, BigDecimal> actualSubjectPriorities = result.getSubjectPriorities();
        Assertions.assertFalse(actualSubjectPriorities.isEmpty());
        Assertions.assertEquals(BigDecimal.valueOf(0.351102957975), actualSubjectPriorities.get(b));
        Assertions.assertEquals(BigDecimal.valueOf(0.269969403869), actualSubjectPriorities.get(c));
        Assertions.assertEquals(BigDecimal.valueOf(0.378933945048), actualSubjectPriorities.get(a));
    }

/*
    @Test
    void computeTest2() {
        Purpose purpose2 = new Purpose();
        purpose2.setId(UUID.randomUUID());
        purpose2.setName("Job");

        Criterion money = dataHelper.getCriterion("money");
        Criterion task = dataHelper.getCriterion("tasks");
        Criterion transport = dataHelper.getCriterion("transport");
        Criterion team = dataHelper.getCriterion("team");
        Criterion medic = dataHelper.getCriterion("medic");
        Criterion company = dataHelper.getCriterion("company");
        Criterion bonuses = dataHelper.getCriterion("bonuses");
        Criterion remote = dataHelper.getCriterion("remote work");

        Set<CriterionRelation> criterionRelations = new HashSet<>();
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, money, task, Estimation.ONE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, money, transport, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, money, team, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, money, medic, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, money, company, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, money, bonuses, Estimation.SEVEN));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, money, remote, Estimation.SEVEN));

        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, task, transport, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, task, team, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, task, medic, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, task, company, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, task, bonuses, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, task, remote, Estimation.FIVE));

        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, transport, team, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, transport, medic, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, transport, company, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, transport, bonuses, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, transport, remote, Estimation.FIVE));

        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, team, medic, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, team, company, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, team, bonuses, Estimation.FIVE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, team, remote, Estimation.FIVE));

        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, medic, company, Estimation.TWO));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, medic, bonuses, Estimation.THREE));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, medic, remote, Estimation.THREE));

        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, company, bonuses, Estimation.TWO));
        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, company, remote, Estimation.THREE));

        criterionRelations.add(dataHelper.buildCriterionRelation(purpose2, bonuses, remote, Estimation.THREE));

        purpose2.setCriterionRelations(criterionRelations);


        Subject green = dataHelper.getSubject("Green");
        Subject red = dataHelper.getSubject("Red");

        Set<SubjectRelation> subjectRelations = new HashSet<>();
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose2, money, red, green, Estimation.THREE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose2, task, red, green , Estimation.FOUR));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose2, transport, green, red, Estimation.THREE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose2, team, green, red, Estimation.TWO));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose2, medic, green, red, Estimation.TWO));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose2, company, green, red, Estimation.FIVE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose2, bonuses, green, red, Estimation.FIVE));
        subjectRelations.add(dataHelper.buildSubjectRelation(purpose2, remote, green, red, Estimation.TWO));

        purpose2.setSubjectRelations(subjectRelations);


        doReturn(purpose2).when(purposeService).getOne(any());
        doReturn(new ArrayList<>()).when(purposeService).check(any());

        Result result = resultService.compute(UUID.randomUUID());

    }
 */

}