package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.papapers.optimalchoice.api.domain.Result;
import ru.papapers.optimalchoice.api.domain.SubjectDto;
import ru.papapers.optimalchoice.api.domain.errors.ComputationError;
import ru.papapers.optimalchoice.api.domain.errors.ErrorCode;
import ru.papapers.optimalchoice.api.domain.errors.SubjectComputationError;
import ru.papapers.optimalchoice.domain.math.MathContext;
import ru.papapers.optimalchoice.domain.math.MatrixMiddleConsistency;
import ru.papapers.optimalchoice.domain.math.Vector;
import ru.papapers.optimalchoice.mapper.CriterionMapper;
import ru.papapers.optimalchoice.mapper.SubjectMapper;
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
    private final SubjectMapper subjectMapper;

    @Autowired
    public ResultService(PurposeService purposeService, CriterionMapper criterionMapper, SubjectMapper subjectMapper) {
        this.purposeService = purposeService;
        this.criterionMapper = criterionMapper;
        this.subjectMapper = subjectMapper;
    }

    public Result compute(UUID purposeId) {
        log.info("Calculation of results has begun. purposeId: {}", purposeId);
        Purpose purpose = purposeService.getOne(purposeId);

        List<Object> errors = purposeService.check(purpose);

        Map<SubjectDto, BigDecimal> subjectPriorities = new HashMap<>();
        if (CollectionUtils.isEmpty(errors)) {
            log.info("Purpose was checked successfully. purposeId: {}", purposeId);
            Map<Criterion, Vector> criterionVectorMap = getCriterionVectors(purpose, errors);
            Map<Subject, Set<Pair<Criterion, BigDecimal>>> subjectVectorMap = getSubjectVectors(purpose, criterionVectorMap, errors);

            subjectVectorMap.forEach((subject, pairs) -> {
                BigDecimal priority = pairs.stream()
                        .map(pair -> {
                            Vector vector = criterionVectorMap.get(pair.getFirst());
                            BigDecimal criterionNormalizeValue = vector.getContext().getNormalizeValue();
                            return pair.getSecond().multiply(criterionNormalizeValue);
                        })
                        .reduce(BigDecimal::add)
                        .orElseThrow(() -> {
                            String msg = String.format("Arithmetic error. purposeId %s, subjectId %s", purpose.getId(), subject.getId());
                            return new ArithmeticException(msg);
                        });

                subjectPriorities.put(subjectMapper.mapToDto(subject), priority);
            });
        } else {
            log.error("Purpose failed the check. purposeId: {}", purposeId);
        }

        return Result.builder()
                .subjectPriorities(subjectPriorities)
                .errors(errors)
                .build();
    }

    private Map<Criterion, Vector> getCriterionVectors(Purpose purpose, List<Object> errors) {
        log.info("Calculation of criterion vectors. purposeId: {}", purpose.getId());
        Set<CriterionRelation> criterionRelations = purpose.getCriterionRelations();
        Set<Criterion> purposeCriteria = Collections.unmodifiableSet(purposeService.getPurposeCriteria(purpose));
        Map<Criterion, Vector> criterionVectorMap = new HashMap<>();
        int criterionCount = purposeCriteria.size();

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

        return criterionVectorMap;
    }

    private Map<Subject, Set<Pair<Criterion, BigDecimal>>> getSubjectVectors(Purpose purpose,
                                                                             Map<Criterion, Vector> criterionVectorMap,
                                                                             List<Object> errors) {
        log.info("Calculation of subject vectors. purposeId: {}", purpose.getId());
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

        return subjectVectorMap;
    }

    private BigDecimal getConsistencyRelation(List<MathContext> contextList, int objectCount) {
        BigDecimal nonNormalizeVectorSum = contextList.stream()
                .map(MathContext::getNonNormalizeValue)
                .reduce(BigDecimal::add)
                .orElseThrow(ArithmeticException::new);

        BigDecimal consistencyIndexComponent = contextList.stream()
                .map(context -> context.computeNormalizeValue(nonNormalizeVectorSum).multiply(context.getColumnValueSum()))
                .reduce(BigDecimal::add)
                .orElseThrow(RuntimeException::new);

        BigDecimal consistencyIndex = consistencyIndexComponent
                .subtract(BigDecimal.valueOf(objectCount))
                .divide(BigDecimal.valueOf(objectCount - 1), SCALE, UP);

        BigDecimal middleMatrixConsistency = Objects.requireNonNull(
                MatrixMiddleConsistency.findByMatrixSize(objectCount),
                "middleMatrixConsistency can't be NULL"
        ).getConsistency();

        return consistencyIndex
                .divide(middleMatrixConsistency, SCALE, UP)
                .multiply(BigDecimal.valueOf(100));
    }

}
