package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.papapers.optimalchoice.domain.CriterionRelationDto;
import ru.papapers.optimalchoice.mapper.CriterionRelationMapper;
import ru.papapers.optimalchoice.model.Criterion;
import ru.papapers.optimalchoice.model.CriterionRelation;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.repository.CriterionRelationRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CriterionRelationService {

    private final CriterionRelationRepository criterionRelationRepository;
    private final CriterionService criterionService;
    private final CriterionRelationMapper mapper;

    @Autowired
    public CriterionRelationService(CriterionRelationRepository criterionRelationRepository,
                                    CriterionService criterionService,
                                    CriterionRelationMapper mapper) {
        this.criterionRelationRepository = criterionRelationRepository;
        this.criterionService = criterionService;
        this.mapper = mapper;
    }

    @Transactional
    public List<CriterionRelation> add(Set<CriterionRelationDto> relations, Purpose purpose) {
        if (CollectionUtils.isEmpty(relations)) return Collections.EMPTY_LIST;

        log.info("Add {} criterion relations for {} purpose", relations.size(), purpose.getName());
        return criterionRelationRepository.saveAll(relations.stream()
                .map(relationDto -> create(relationDto, purpose))
                .collect(Collectors.toSet()));
    }

    @Transactional
    public CriterionRelation addOne(CriterionRelationDto relationDto, Purpose purpose) {
        log.info("Add new criterion relations for purpose {}", purpose.getName());
        CriterionRelation relation = create(relationDto, purpose);

        return criterionRelationRepository.save(relation);
    }

    private CriterionRelation create(CriterionRelationDto relationDto, Purpose purpose) {
        CriterionRelation relation = mapper.mapToEntity(relationDto);

        relation.setPurpose(purpose);
        Criterion criterionFromDB = criterionService.add(relation.getCriterion());
        Criterion comparingCriterionFromDB = criterionService.add(relation.getComparingCriterion());


        Optional<CriterionRelation> relationOptionalFromDB = getFromDB(purpose, criterionFromDB, comparingCriterionFromDB);
        if (relationOptionalFromDB.isPresent()) {
            CriterionRelation relationFromDB = relationOptionalFromDB.get();
            relationFromDB.setEstimation(relation.getEstimation());

            return relationFromDB;
        } else {
            relation.setCriterion(criterionFromDB);
            relation.setComparingCriterion(comparingCriterionFromDB);

            return relation;
        }
    }

    private Optional<CriterionRelation> getFromDB(Purpose purpose, Criterion criterion, Criterion comparingCriterion) {
        Optional<CriterionRelation> relationOptional = criterionRelationRepository
                .findByPurposeAndCriterionAndComparingCriterion(purpose, criterion, comparingCriterion)
                .stream()
                .findFirst();

        if (relationOptional.isPresent()) {
            return relationOptional;
        } else {
            return criterionRelationRepository
                    .findByPurposeAndCriterionAndComparingCriterion(purpose, comparingCriterion, criterion)
                    .stream()
                    .findFirst();
        }
    }
}
