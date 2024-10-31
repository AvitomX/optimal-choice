package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.papapers.optimalchoice.api.domain.CriterionRelationDto;
import ru.papapers.optimalchoice.mapper.CriterionRelationMapper;
import ru.papapers.optimalchoice.model.Criterion;
import ru.papapers.optimalchoice.model.CriterionRelation;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.repository.CriterionRelationRepository;
import ru.papapers.optimalchoice.api.domain.errors.ErrorCode;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.papapers.optimalchoice.api.domain.errors.ErrorCode.CRITERION_COMPARING_ERROR;
import static ru.papapers.optimalchoice.api.domain.errors.ErrorCode.CRITERION_COUNT_ERROR;


@Service
@Slf4j
public class CriterionRelationService {

    public static final int MIN_CRITERION_COUNT = 2;

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

    @Transactional
    public CriterionRelation update(CriterionRelationDto criterionRelationDto) {
        CriterionRelation relation = mapper.mapToEntity(criterionRelationDto);

        UUID id = relation.getId();
        CriterionRelation relationFromDB = criterionRelationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Criterion relation with ID = " + id + " not exists"));

        relationFromDB.setCriterion(criterionService.add(relation.getCriterion()));
        relationFromDB.setComparingCriterion(criterionService.add(relation.getComparingCriterion()));
        relationFromDB.setEstimation(criterionRelationDto.getEstimation());

        return relationFromDB;
    }

    @Transactional
    public void delete(UUID id) {
        criterionRelationRepository.deleteById(id);
        log.info("Criterion relation was deleted. criterionRelationId: {}", id);
    }

    public List<Object> check(Set<CriterionRelation> criterionRelations) {
        List<Object> errors = new ArrayList<>();

        Set<Criterion> purposeCriteria = new HashSet<>();
        List<Criterion> comparingCriteria = new ArrayList<>();

        criterionRelations.forEach(criterionRelation -> {
            purposeCriteria.add(criterionRelation.getCriterion());
            purposeCriteria.add(criterionRelation.getComparingCriterion());

            comparingCriteria.add(criterionRelation.getCriterion());
            comparingCriteria.add(criterionRelation.getComparingCriterion());
        });

        if (purposeCriteria.size() < MIN_CRITERION_COUNT) {
            errors.add(criterionService.createCriterionError(null, CRITERION_COUNT_ERROR));
            return errors;
        }

        purposeCriteria.forEach(criterion -> {
            int frequency = Collections.frequency(comparingCriteria, criterion);
            if (frequency != purposeCriteria.size() - 1) {
                errors.add(criterionService.createCriterionError(criterion, CRITERION_COMPARING_ERROR));
            }
        });

        return errors;
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
