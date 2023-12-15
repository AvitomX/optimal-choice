package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.papapers.optimalchoice.domain.Result;
import ru.papapers.optimalchoice.domain.errors.ComputationError;
import ru.papapers.optimalchoice.domain.errors.ErrorCode;
import ru.papapers.optimalchoice.domain.errors.SubjectComputationError;
import ru.papapers.optimalchoice.domain.math.MathContext;
import ru.papapers.optimalchoice.domain.math.MatrixMiddleConsistency;
import ru.papapers.optimalchoice.domain.math.Vector;
import ru.papapers.optimalchoice.mapper.CriterionMapper;
import ru.papapers.optimalchoice.model.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.RoundingMode.UP;
import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class ResultService {

    public static final int SCALE = 6;

    private final PurposeService purposeService;
    private final CriterionMapper criterionMapper;

    @Autowired
    public ResultService(PurposeService purposeService, CriterionMapper criterionMapper) {
        this.purposeService = purposeService;
        this.criterionMapper = criterionMapper;
    }

    public Result compute(UUID purposeId) {
        Purpose purpose = purposeService.getOne(purposeId);

        List<Object> errors = purposeService.check(purpose);
        if (CollectionUtils.isEmpty(errors)) {
            Set<CriterionRelation> criterionRelations = purpose.getCriterionRelations();
            Set<Criterion> purposeCriteria = Collections.unmodifiableSet(purposeService.getPurposeCriteria(purpose));

            int criterionCount = purposeCriteria.size();
            Map<Criterion, Vector> criterionVectorMap = new HashMap<>();
            purposeCriteria.forEach(criterion -> {
                Vector vector = new Vector(criterion, criterionCount);
                MathContext context = vector.getContext();
                criterionRelations.forEach(relation -> {
                    if (criterion.equals(relation.getCriterion())) {
                        context.addRowValue(relation.getEstimation().getDirectValue());
                        context.addColumnValue(relation.getEstimation().getReverseValue());
                    }
                    if (criterion.equals(relation.getComparingCriterion())) {
                        context.addRowValue(relation.getEstimation().getReverseValue());
                        context.addColumnValue(relation.getEstimation().getDirectValue());
                    }
                });

                criterionVectorMap.put(vector.getCriterion(), vector);
            });

            List<MathContext> contextList = criterionVectorMap.values().stream()
                    .map(Vector::getContext)
                    .collect(Collectors.toList());
            BigDecimal criterionConsistencyRelation = getConsistencyRelation(contextList, criterionCount);

            if (criterionConsistencyRelation.compareTo(BigDecimal.valueOf(20)) > 0) {
                ComputationError error = new ComputationError(ErrorCode.CRITERIA_CONSISTENCY_ERROR.getCode(),
                        ErrorCode.CRITERIA_CONSISTENCY_ERROR.getMessage());
                errors.add(error);
            }


            Set<Subject> purposeSubject = Collections.unmodifiableSet(purposeService.getPurposeSubject(purpose));
            Map<Criterion, List<SubjectRelation>> criterionListMap = purpose.getSubjectRelations().stream()
                    .collect(groupingBy(SubjectRelation::getCriterion));

            criterionListMap.forEach((criterion, subjectRelations) -> {
                Vector vector = criterionVectorMap.get(criterion);
                purposeSubject.forEach(subject -> {
                    MathContext context = new MathContext(purposeSubject.size());
                    subjectRelations.forEach(relation -> {
                        if (subject.equals(relation.getSubject())) {
                            context.addRowValue(relation.getEstimation().getDirectValue());
                            context.addColumnValue(relation.getEstimation().getReverseValue());
                        }
                        if (subject.equals(relation.getComparingSubject())) {
                            context.addRowValue(relation.getEstimation().getReverseValue());
                            context.addColumnValue(relation.getEstimation().getDirectValue());
                        }
                    });
                    vector.addSubjectContext(subject, context);
                });
            });

            Map<Subject, Set<Pair<Criterion, BigDecimal>>> subjectVectorMap = new HashMap<>();
            criterionVectorMap.values().forEach(vector -> {
                List<MathContext> contexts = new ArrayList<>(vector.getSubjectContextMap().values());
                BigDecimal subjectConsistencyRelation = getConsistencyRelation(contexts, purposeSubject.size());
                if (subjectConsistencyRelation.compareTo(BigDecimal.valueOf(20)) > 0) {
                    ComputationError error = new SubjectComputationError(
                            ErrorCode.SUBJECT_CONSISTENCY_ERROR.getCode(),
                            ErrorCode.SUBJECT_CONSISTENCY_ERROR.getMessage(),
                            criterionMapper.mapToDto(vector.getCriterion())
                    );
                    errors.add(error);
                }

                vector.getSubjectContextMap().forEach((subject, context) -> {
                    Set<Pair<Criterion, BigDecimal>> pairs = subjectVectorMap.get(subject);
                    if (pairs == null) {
                        pairs = new HashSet<>();
                        pairs.add(Pair.of(vector.getCriterion(), context.getNormalizeValue()));
                        subjectVectorMap.put(subject, pairs);
                    } else {
                        pairs.add(Pair.of(vector.getCriterion(), context.getNormalizeValue()));
                    }
                });
            });

            Map<Subject, BigDecimal> priorityList = new HashMap<>();
            subjectVectorMap.forEach((subject, pairs) -> {
                BigDecimal priority = pairs.stream()
                        .map(pair -> {
                            Vector vector = criterionVectorMap.get(pair.getFirst());
                            BigDecimal criterionNormalizeValue = vector.getContext().getNormalizeValue();
                            return pair.getSecond().multiply(criterionNormalizeValue);
                        })
                        .reduce(BigDecimal::add)
                        .orElseThrow(RuntimeException::new);//TODO

                priorityList.put(subject, priority);
            });


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
                .map(context -> context.computeNormalizeValue(nonNormalizeVectorSum).multiply(context.getColumnValueSum()))
                .reduce(BigDecimal::add)
                .orElseThrow(RuntimeException::new);

        BigDecimal consistencyIndex = consistencyIndexComponent
                .subtract(BigDecimal.valueOf(objectCount))
                .divide(BigDecimal.valueOf(objectCount - 1), SCALE, UP);

        BigDecimal middleMatrixConsistency = Objects.requireNonNull(
                MatrixMiddleConsistency.findByMatrixSize(objectCount)).getConsistency(); //TODO

        return consistencyIndex
                .divide(middleMatrixConsistency, SCALE, UP)
                .multiply(BigDecimal.valueOf(100));
    }

}
