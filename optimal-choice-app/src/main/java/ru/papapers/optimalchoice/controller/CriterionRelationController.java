package ru.papapers.optimalchoice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.papapers.optimalchoice.domain.CriterionRelationDto;
import ru.papapers.optimalchoice.mapper.CriterionRelationMapper;
import ru.papapers.optimalchoice.model.CriterionRelation;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.service.CriterionRelationService;
import ru.papapers.optimalchoice.service.PurposeService;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/criterion-relation")
public class CriterionRelationController {
    private final CriterionRelationService criterionRelationService;
    private final CriterionRelationMapper mapper;
    private final PurposeService purposeService;

    @Autowired
    public CriterionRelationController(CriterionRelationService criterionRelationService,
                                       CriterionRelationMapper mapper,
                                       PurposeService purposeService) {
        this.criterionRelationService = criterionRelationService;
        this.mapper = mapper;
        this.purposeService = purposeService;
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CriterionRelationDto> create(@RequestParam(value = "purpose-id") UUID purposeId,
                                                       @Valid @RequestBody CriterionRelationDto criterionRelationDto) {
        log.info("Request for creation of new criterion relation was accepted.");

        Purpose purpose = purposeService.getOne(purposeId);
        CriterionRelation criterionRelation = criterionRelationService.addOne(criterionRelationDto, purpose);

        return new ResponseEntity<>(mapper.mapToDto(criterionRelation), HttpStatus.CREATED);
    }
}
