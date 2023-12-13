package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.papapers.optimalchoice.domain.math.MathContext;
import ru.papapers.optimalchoice.domain.math.MatrixMiddleConsistency;
import ru.papapers.optimalchoice.domain.Result;
import ru.papapers.optimalchoice.domain.errors.ComputationError;
import ru.papapers.optimalchoice.domain.errors.ErrorCode;
import ru.papapers.optimalchoice.domain.math.Vector;
import ru.papapers.optimalchoice.model.Criterion;
import ru.papapers.optimalchoice.model.CriterionRelation;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.model.SubjectRelation;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.RoundingMode.HALF_UP;

@Slf4j
@Service
public class ResultService {

    public static final int SCALE = 6;

    private final PurposeService purposeService;

    @Autowired
    public ResultService(PurposeService purposeService) {
        this.purposeService = purposeService;
    }

    public Result compute(UUID purposeId) {
        Purpose purpose = purposeService.getOne(purposeId);

        List<Object> errors = purposeService.check(purpose);
        if (CollectionUtils.isEmpty(errors)) {
            Set<CriterionRelation> criterionRelations = purpose.getCriterionRelations();
            Set<Criterion> purposeCriteria = new HashSet<>();
            criterionRelations.forEach(relation -> {
                purposeCriteria.add(relation.getCriterion());
                purposeCriteria.add(relation.getComparingCriterion());
            });

            Map<Criterion, List<SubjectRelation>> criterionListMap = purpose.getSubjectRelations().stream()
                    .collect(Collectors.groupingBy(SubjectRelation::getCriterion));

            int criterionCount = purposeCriteria.size();
            Set<Vector> criterionVectors = new HashSet<>();
            purposeCriteria.forEach(criterion -> {
                Vector vector = new Vector(criterion, criterionCount);
                criterionRelations.forEach(relation -> {
                    if (criterion.equals(relation.getCriterion())) {
                        vector.getContext().addRowValue(relation.getEstimation().getDirectValue());
                        vector.getContext().addColumnValue(relation.getEstimation().getReverseValue());
                    }
                    if (criterion.equals(relation.getComparingCriterion())) {
                        vector.getContext().addRowValue(relation.getEstimation().getReverseValue());
                        vector.getContext().addColumnValue(relation.getEstimation().getDirectValue());
                    }
                });

                criterionVectors.add(vector);
            });

            List<MathContext> contextList = criterionVectors.stream().map(Vector::getContext).collect(Collectors.toList());
            BigDecimal criterionConsistencyRelation = getConsistencyRelation(contextList, criterionCount);

            if (criterionConsistencyRelation.compareTo(BigDecimal.valueOf(20)) > 0) {
                ComputationError error = new ComputationError(ErrorCode.CRITERIA_CONSISTENCY_ERROR.getCode(),
                        ErrorCode.CRITERIA_CONSISTENCY_ERROR.getMessage());
                errors.add(error);
            }


            System.out.println();
        }

        return Result.builder()
                .errors(errors)
                .build();
    }

    private BigDecimal getConsistencyRelation(List<MathContext> contextList, int objectCount) {
        BigDecimal nonNormalizeVectorSum = contextList.stream()
                .map(MathContext::getNonNormalizeValue)
                .reduce(BigDecimal::add)
                .orElseThrow(RuntimeException::new); //TODO

        BigDecimal consistencyIndexComponent = contextList.stream()
                .map(context -> context.getNormalizeValue(nonNormalizeVectorSum).multiply(context.getColumnValueSum()))
                .reduce(BigDecimal::add)
                .orElseThrow(RuntimeException::new);

        BigDecimal consistencyIndex = consistencyIndexComponent
                .subtract(BigDecimal.valueOf(objectCount))
                .divide(BigDecimal.valueOf(objectCount - 1), SCALE, HALF_UP);

        BigDecimal middleMatrixConsistency = Objects.requireNonNull(
                MatrixMiddleConsistency.findByMatrixSize(objectCount)).getConsistency(); //TODO

        return consistencyIndex
                .divide(middleMatrixConsistency, SCALE, HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
//
//    private class Vector {
//
//        public static final double ONE = 1.0;
//
//        private final Criterion criterion;
//
//        private final int purposeCriterionCount;
//        private final List<BigDecimal> row = new ArrayList<>();
//        private final List<BigDecimal> column = new ArrayList<>();
//        private BigDecimal nonNormalizeValue;
//        private BigDecimal normalizeValue;
//
//        public Vector(Criterion criterion, int purposeCriterionCount) {
//            this.criterion = criterion;
//            this.purposeCriterionCount = purposeCriterionCount;
//        }
//
//        public BigDecimal getNonNormalizeValue() {
//            if (nonNormalizeValue == null) {
//                BigDecimal multiplication = row.stream().reduce(BigDecimal::multiply).orElseThrow(RuntimeException::new);
//                this.nonNormalizeValue = BigDecimal.valueOf(Math.pow(multiplication.doubleValue(), ONE / purposeCriterionCount));
//            }
//
//            return this.nonNormalizeValue;
//        }
//
//        public BigDecimal getNormalizeValue(BigDecimal nonNormalizeVectorSum) {
//            if (normalizeValue == null) {
//                this.normalizeValue = getNonNormalizeValue().divide(nonNormalizeVectorSum, SCALE, HALF_UP);
//            }
//
//            return this.normalizeValue;
//        }
//
//        public BigDecimal getColumnValueSum() {
//            return column.stream()
//                    .reduce(BigDecimal::add)
//                    .orElseThrow(RuntimeException::new)
//                    .add(BigDecimal.ONE); //TODO
//        }
//    }

}
