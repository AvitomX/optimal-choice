package ru.papapers.optimalchoice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.papapers.optimalchoice.api.domain.CriterionRelationDto;
import ru.papapers.optimalchoice.mapper.CriterionRelationMapper;
import ru.papapers.optimalchoice.model.CriterionRelation;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.service.CriterionRelationService;
import ru.papapers.optimalchoice.service.PurposeService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
//TODO @RequestMapping("/criterion-relation")
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

    @PostMapping(value = "purpose/{purposeId}/criterion-relations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<CriterionRelationDto>> create(@PathVariable("purposeId") UUID purposeId,
                                                            @Valid @RequestBody Set<CriterionRelationDto> criterionRelationsDto) {
        log.info("Request for creation of {} new criterion relations for purpose {} was accepted.",
                criterionRelationsDto.size(), purposeId);

        Purpose purpose = purposeService.getOne(purposeId);
        List<CriterionRelation> criterionRelations = criterionRelationService.add(criterionRelationsDto, purpose);

        return new ResponseEntity<>(criterionRelations.stream().map(mapper::mapToDto).collect(Collectors.toSet()),
                HttpStatus.CREATED);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CriterionRelationDto> update(@Valid @RequestBody CriterionRelationDto criterionRelationDto) {
        log.info("Request for modification of criterion relation was accepted.");
        CriterionRelation criterionRelation = criterionRelationService.update(criterionRelationDto);

        return new ResponseEntity<>(mapper.mapToDto(criterionRelation), HttpStatus.OK);
    }

    @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> delete(@PathVariable("id") UUID id) {
        log.info("Request for criterion relation deleting with id: {} was accepted.", id);
        criterionRelationService.delete(id);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
