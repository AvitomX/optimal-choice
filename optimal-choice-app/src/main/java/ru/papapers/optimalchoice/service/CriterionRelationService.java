package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.papapers.optimalchoice.domain.CriterionRelationDto;
import ru.papapers.optimalchoice.mapper.CriterionRelationMapper;
import ru.papapers.optimalchoice.model.CriterionRelation;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.repository.CriterionRelationRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
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
                .map(relationDto -> {
                    CriterionRelation relation = mapper.mapToEntity(relationDto);

                    relation.setPurpose(purpose);

                    relation.setCriterion(criterionService.add(relation.getCriterion()));
                    relation.setComparingCriterion(criterionService.add(relation.getComparingCriterion()));

                    return relation;
                })
                .collect(Collectors.toSet()));
    }
}
