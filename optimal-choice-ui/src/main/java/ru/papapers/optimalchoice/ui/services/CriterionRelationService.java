package ru.papapers.optimalchoice.ui.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.papapers.optimalchoice.api.domain.CriterionDto;
import ru.papapers.optimalchoice.api.domain.CriterionRelationDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CriterionRelationService {

    private final CriterionService criterionService;

    public CriterionRelationService(CriterionService criterionService) {
        this.criterionService = criterionService;
    }

    public Set<CriterionRelationDto> getCriterionRelationByPurposeId(UUID purposeId) {
        Set<CriterionDto> criteria = criterionService.getAllByPurpose(purposeId);
        List<CriterionDto> criteriaList = criteria.stream().toList();
        Set<CriterionRelationDto> criterionRelations = new HashSet<>();

        for (int i = 0; i < criteriaList.size(); i++) {
            CriterionDto criterion = criteriaList.get(i);
            for (int j = i + 1; j < criteriaList.size(); j++) {
                CriterionDto comparingCriterion = criteriaList.get(j);
                CriterionRelationDto criterionRelation = new CriterionRelationDto();
                criterionRelation.setCriterion(criterion);
                criterionRelation.setComparingCriterion(comparingCriterion);
                criterionRelations.add(criterionRelation);
            }
        }

        return criterionRelations;
    }
}
