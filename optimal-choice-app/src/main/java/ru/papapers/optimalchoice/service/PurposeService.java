package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.papapers.optimalchoice.domain.PurposeDto;
import ru.papapers.optimalchoice.model.*;
import ru.papapers.optimalchoice.repository.PurposeRepository;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Slf4j
@Service
public class PurposeService {

    private final PurposeRepository purposeRepository;
    private final CriterionRelationService criterionRelationService;
    private final SubjectRelationService subjectRelationService;

    @Autowired
    public PurposeService(PurposeRepository purposeRepository,
                          CriterionRelationService criterionRelationService,
                          SubjectRelationService subjectRelationService) {
        this.purposeRepository = purposeRepository;
        this.criterionRelationService = criterionRelationService;
        this.subjectRelationService = subjectRelationService;
    }

    @Transactional
    public Purpose create(PurposeDto purposeDto) {
        log.info("Create \"{}\" purpose and save it into DB.", purposeDto.getName());

        Purpose purpose = new Purpose();
        purpose.setName(purposeDto.getName());
        Purpose purposeFromDB = purposeRepository.save(purpose);

        List<CriterionRelation> criterionRelationsFromDB = criterionRelationService.add(purposeDto.getCriterionRelations(), purposeFromDB);
        purposeFromDB.setCriterionRelations(new HashSet<>(criterionRelationsFromDB));

        List<SubjectRelation> subjectRelationsFromDB = subjectRelationService.add(purposeDto.getSubjectRelations(), purposeFromDB);
        purposeFromDB.setSubjectRelations(new HashSet<>(subjectRelationsFromDB));

        return purposeFromDB;
    }

    @Transactional(readOnly = true)
    public Purpose getOne(UUID id) {
        log.info("Get purpose by id: {}.", id);
        return purposeRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purpose with ID = " + id + " not exists"));
    }

    public List<Object> check(Purpose purpose) {
        Set<CriterionRelation> criterionRelations = purpose.getCriterionRelations();
        List<Object> errors = criterionRelationService.check(criterionRelations);

        Set<SubjectRelation> subjectRelations = purpose.getSubjectRelations();
        List<Object> subjectApiErrors = subjectRelationService.check(subjectRelations);

        errors.addAll(subjectApiErrors);

        return errors;
    }

    public Set<Criterion> getPurposeCriteria(Purpose purpose) {
        Set<Criterion> criteria = new HashSet<>();
        purpose.getCriterionRelations().forEach(relation -> {
            criteria.add(relation.getCriterion());
            criteria.add(relation.getComparingCriterion());
        });

        return criteria;
    }

    public Set<Subject> getPurposeSubject(Purpose purpose) {
        Set<Subject> subjects = new HashSet<>();
        purpose.getSubjectRelations().forEach(relation -> {
            subjects.add(relation.getSubject());
            subjects.add(relation.getComparingSubject());
        });

        return subjects;
    }
}
