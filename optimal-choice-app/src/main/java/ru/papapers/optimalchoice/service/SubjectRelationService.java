package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.papapers.optimalchoice.domain.SubjectRelationDto;
import ru.papapers.optimalchoice.mapper.SubjectRelationMapper;
import ru.papapers.optimalchoice.model.*;
import ru.papapers.optimalchoice.repository.SubjectRelationRepository;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.papapers.optimalchoice.domain.errors.ErrorCode.*;

@Service
@Slf4j
public class SubjectRelationService {

    public static final int MIN_SUBJECT_COUNT = 2;

    private final SubjectRelationRepository repository;
    private final CriterionService criterionService;
    private final SubjectService subjectService;
    private final SubjectRelationMapper mapper;

    @Autowired
    public SubjectRelationService(SubjectRelationRepository repository,
                                  CriterionService criterionService,
                                  SubjectService subjectService,
                                  SubjectRelationMapper mapper) {
        this.repository = repository;
        this.criterionService = criterionService;
        this.subjectService = subjectService;
        this.mapper = mapper;
    }

    @Transactional
    public List<SubjectRelation> add(Set<SubjectRelationDto> relations, Purpose purpose) {
        if (CollectionUtils.isEmpty(relations)) return Collections.EMPTY_LIST;

        log.info("Add {} subject relations for {} purpose", relations.size(), purpose.getName());
        return repository.saveAll(relations.stream()
                .map(relationDto -> create(relationDto, purpose))
                .collect(Collectors.toList()));
    }

    @Transactional
    public SubjectRelation addOne(SubjectRelationDto subjectRelationDto, Purpose purpose) {
        log.info("Add new subject relation for purpose {}", purpose.getName());

        return repository.save(create(subjectRelationDto, purpose));
    }

    private SubjectRelation create(SubjectRelationDto dto, Purpose purpose) {
        SubjectRelation relation = mapper.mapToEntity(dto);

        relation.setPurpose(purpose);

        String criterionName = relation.getCriterion().getName();
        Criterion criterionFromDB = criterionService.getOneByName(criterionName)
                .orElseThrow(() -> new EntityNotFoundException("Criterion with name = " + criterionName + " not exists"));
        relation.setCriterion(criterionFromDB);

        relation.setSubject(subjectService.add(relation.getSubject()));
        relation.setComparingSubject(subjectService.add(relation.getComparingSubject()));

        Optional<SubjectRelation> relationOptionalFromDB = getFromDB(relation);
        if (relationOptionalFromDB.isPresent()) {
            SubjectRelation subjectRelationFromDB = relationOptionalFromDB.get();
            subjectRelationFromDB.setEstimation(relation.getEstimation());

            return subjectRelationFromDB;
        }

        return relation;
    }

    private Optional<SubjectRelation> getFromDB(SubjectRelation relation) {
        Purpose purpose = relation.getPurpose();
        Criterion criterion = relation.getCriterion();
        Subject subject = relation.getSubject();
        Subject comparingSubject = relation.getComparingSubject();

        Optional<SubjectRelation> relationOptional = repository
                .findByPurposeAndCriterionAndSubjectAndComparingSubject(purpose, criterion, subject, comparingSubject)
                .stream()
                .findFirst();

        if (relationOptional.isPresent()) {
            return relationOptional;
        } else {
            return repository
                    .findByPurposeAndCriterionAndSubjectAndComparingSubject(purpose, criterion, comparingSubject, subject)
                    .stream()
                    .findFirst();
        }
    }

    public List<Object> check(Set<SubjectRelation> relations) {
        Set<Criterion> purposeCriteria = getPurposeCriteria(relations);
        Set<Subject> purposeSubjects = getPurposeSubjects(relations);

        List<Object> errors = new ArrayList<>();
        if (purposeSubjects.size() < MIN_SUBJECT_COUNT) {
            errors.add(subjectService.createSubjectError(null, null, SUBJECT_COUNT_ERROR));
            return errors;
        }

        errors.addAll(checkSubjectRelationCriteria(purposeCriteria, relations)) ;

        Map<Criterion, List<Pair<Subject, Subject>>> map = relations.stream()
                .collect(Collectors.groupingBy(SubjectRelation::getCriterion,
                Collectors.mapping(sbjRlt -> Pair.of(sbjRlt.getSubject(), sbjRlt.getComparingSubject()), Collectors.toList())));

        map.forEach((criterion, pairs) -> {
            List<Subject> subjects = new ArrayList<>();
            pairs.forEach(pair -> {
                subjects.add(pair.getFirst());
                subjects.add(pair.getSecond());
            });

            purposeSubjects.forEach(purposeSubject -> {
                int frequency = Collections.frequency(subjects, purposeSubject);
                if (frequency != purposeSubjects.size() - 1) {
                    errors.add(subjectService.createSubjectError(purposeSubject, criterion, SUBJECT_COMPARING_ERROR));
                }
            });
        });

        return errors;
    }

    private Set<Criterion> getPurposeCriteria(Set<SubjectRelation> subjectRelations) {
        Set<Criterion> criteriaSet = new HashSet<>();

        SubjectRelation subjectRelation = subjectRelations.iterator().next();
        Set<CriterionRelation> criterionRelations = subjectRelation.getPurpose().getCriterionRelations();

        criterionRelations.forEach(criterionRelation -> {
            criteriaSet.add(criterionRelation.getCriterion());
            criteriaSet.add(criterionRelation.getComparingCriterion());
        });

        return Collections.unmodifiableSet(criteriaSet) ;
    }


    private Set<Subject> getPurposeSubjects(Set<SubjectRelation> relations) {
        HashSet<Subject> subjects = new HashSet<>();
        relations.forEach(subjectRelation -> {
            subjects.add(subjectRelation.getSubject());
            subjects.add(subjectRelation.getComparingSubject());
        });

        return Collections.unmodifiableSet(subjects);
    }


    private List<Object> checkSubjectRelationCriteria(Set<Criterion> criteria, Set<SubjectRelation> relations) {
        List<Object> errors = new ArrayList<>();

        Set<Criterion> subjectRelationCriteria = relations.stream()
                .map(SubjectRelation::getCriterion)
                .collect(Collectors.toSet());

        criteria.stream()
                .filter(Predicate.not(subjectRelationCriteria::contains))
                .forEach(criterion ->
                        errors.add(criterionService.createCriterionError(criterion, CRITERION_SUBJECT_RELATIONS_ABSENCE_ERROR)));

        return errors;
    }
}
