package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.papapers.optimalchoice.api.domain.PurposeDto;
import ru.papapers.optimalchoice.model.*;
import ru.papapers.optimalchoice.repository.PurposeRepository;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class PurposeService {

    private final PurposeRepository purposeRepository;
    private final CriterionRelationService criterionRelationService;
    private final SubjectRelationService subjectRelationService;
    private final CriterionService criterionService;

    @Autowired
    public PurposeService(PurposeRepository purposeRepository,
                          CriterionRelationService criterionRelationService,
                          SubjectRelationService subjectRelationService,
                          CriterionService criterionService) {
        this.purposeRepository = purposeRepository;
        this.criterionRelationService = criterionRelationService;
        this.subjectRelationService = subjectRelationService;
        this.criterionService = criterionService;
    }

    @Transactional
    public Purpose create(PurposeDto purposeDto) {
        log.info("Create \"{}\" purpose and save it into DB.", purposeDto.getName());

        Purpose purpose = new Purpose();
        purpose.setName(purposeDto.getName());
        purpose.setCriteria(criterionService.add(purposeDto.getCriteria()));
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

    @Transactional
    public Purpose update(UUID id, PurposeDto newPurposeDto) {
        Purpose purpose = getOne(id);
        purpose.setName(newPurposeDto.getName());
        log.info("Purpose was updated. purposeId: {}", purpose.getId());

        return purpose;
    }

    @Transactional
    public void delete(UUID id) {
        purposeRepository.deleteById(id);
        log.info("Purpose was deleted. purposeId: {}", id);
    }

    @Transactional
    public Purpose addCriterion(Criterion criterion, UUID purposeId) {
        Purpose purpose = getOne(purposeId);
        purpose.getCriteria().add(criterion);
        return purposeRepository.save(purpose);
    }

    @Transactional
    public UUID removeCriterion(UUID id, UUID criterionId) {
        Purpose purpose = getOne(id);
        purpose.getCriteria().removeIf(e -> criterionId.equals(e.getId()));

        return criterionId;
    }

    @Transactional
    public Purpose updateCriteria(Set<Criterion> criteria, UUID purposeId) {
        Purpose purpose = getOne(purposeId);
        purpose.setCriteria(criteria);
        return purposeRepository.save(purpose);
    }

    @Transactional
    public Purpose updateSubjects(Set<Subject> subjects, UUID purposeId) {
        Purpose purpose = getOne(purposeId);
        purpose.setSubjects(subjects);
        return purposeRepository.save(purpose);
    }
}
