package ru.papapers.optimalchoice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.papapers.optimalchoice.DataHelper;
import ru.papapers.optimalchoice.domain.Result;
import ru.papapers.optimalchoice.model.Criterion;
import ru.papapers.optimalchoice.model.CriterionRelation;
import ru.papapers.optimalchoice.model.Estimation;
import ru.papapers.optimalchoice.model.Purpose;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @Mock
    private PurposeService purposeService;

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
    }

    @Test
    void computeTest() {
        when(purposeService.getOne(any())).thenReturn(purpose);
        when(purposeService.check(any())).thenReturn(Collections.EMPTY_LIST);

        Result result = resultService.compute(UUID.randomUUID());

    }
}