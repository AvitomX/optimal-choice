package ru.papapers.optimalchoice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.papapers.optimalchoice.domain.SubjectRelationDto;
import ru.papapers.optimalchoice.mapper.SubjectRelationMapper;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.model.SubjectRelation;
import ru.papapers.optimalchoice.service.PurposeService;
import ru.papapers.optimalchoice.service.SubjectRelationService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/subject-relation")
public class SubjectRelationController {
    private final SubjectRelationService subjectRelationService;
    private final PurposeService purposeService;
    private final SubjectRelationMapper mapper;

    @Autowired
    public SubjectRelationController(SubjectRelationService subjectRelationService,
                                     PurposeService purposeService,
                                     SubjectRelationMapper mapper) {
        this.subjectRelationService = subjectRelationService;
        this.purposeService = purposeService;
        this.mapper = mapper;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SubjectRelationDto> create(@RequestParam(value = "purpose-id") UUID purposeId,
                                                     @RequestBody SubjectRelationDto subjectRelationDto) {
        log.info("Request for creation of new subject relation was accepted.");

        Purpose purpose = purposeService.getOne(purposeId);
        SubjectRelation subjectRelation = subjectRelationService.addOne(subjectRelationDto, purpose);

        return new ResponseEntity<>(mapper.mapToDto(subjectRelation), HttpStatus.CREATED);
    }
}