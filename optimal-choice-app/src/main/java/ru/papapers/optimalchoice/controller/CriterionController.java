package ru.papapers.optimalchoice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.papapers.optimalchoice.api.domain.CriterionDto;
import ru.papapers.optimalchoice.mapper.CriterionMapper;
import ru.papapers.optimalchoice.model.Criterion;
import ru.papapers.optimalchoice.service.CriterionService;
import ru.papapers.optimalchoice.service.PurposeService;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
public class CriterionController {

    private final CriterionMapper criterionMapper;
    private final PurposeService purposeService;
    private final CriterionService criterionService;

    public CriterionController(CriterionMapper criterionMapper,
                               PurposeService purposeService,
                               CriterionService criterionService) {
        this.criterionMapper = criterionMapper;
        this.purposeService = purposeService;
        this.criterionService = criterionService;
    }

    @PostMapping(value = "purpose/{purposeId}/criterion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CriterionDto> addCriterion(@Valid @RequestBody CriterionDto criterionDto,
                                                     @PathVariable("purposeId") UUID purposeId) {
        log.info("Request was accepted to add criterion {} for purpose {}.", criterionDto, purposeId);
        Criterion criterion = criterionService.add(criterionMapper.mapToEntity(criterionDto));
        purposeService.addCriteria(criterion, purposeId);

        return new ResponseEntity<>(criterionMapper.mapToDto(criterion), HttpStatus.OK);
    }

    @DeleteMapping(value = "purpose/{purposeId}/criterion/{id}", produces = MediaType.ALL_VALUE)
    public ResponseEntity<String> deleteCriterion(@PathVariable("id") UUID id, @PathVariable("purposeId") UUID purposeId) {
        log.info("Request was accepted to delete criterion: {} for purpose: {}.", id, purposeId);
        UUID criterionId = purposeService.removeCriterion(purposeId, id);

        return ResponseEntity.ok(criterionId.toString());
    }
}
