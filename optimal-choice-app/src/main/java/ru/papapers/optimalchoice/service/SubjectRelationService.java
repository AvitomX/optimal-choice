package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.papapers.optimalchoice.domain.SubjectRelationDto;
import ru.papapers.optimalchoice.mapper.SubjectRelationMapper;
import ru.papapers.optimalchoice.model.*;
import ru.papapers.optimalchoice.repository.SubjectRelationRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubjectRelationService {

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
}
