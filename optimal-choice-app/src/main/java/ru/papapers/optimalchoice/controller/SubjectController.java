package ru.papapers.optimalchoice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.papapers.optimalchoice.api.domain.SubjectDto;
import ru.papapers.optimalchoice.mapper.SubjectMapper;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.model.Subject;
import ru.papapers.optimalchoice.service.PurposeService;
import ru.papapers.optimalchoice.service.SubjectService;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class SubjectController {

    private final SubjectMapper subjectMapper;
    private final PurposeService purposeService;
    private final SubjectService subjectService;

    @Autowired
    public SubjectController(SubjectMapper subjectMapper,
                             PurposeService purposeService,
                             SubjectService subjectService) {
        this.subjectMapper = subjectMapper;
        this.purposeService = purposeService;
        this.subjectService = subjectService;
    }

    @PostMapping(value = "purpose/{purposeId}/subjects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<SubjectDto>> addSubjects(@Valid @RequestBody Set<SubjectDto> subjectDtos,
                                                          @PathVariable("purposeId") UUID purposeId) {
        log.info("Request was accepted to add {} subjects for purpose {}.", subjectDtos.size(), purposeId);
        Set<Subject> subjectsFromDB = subjectDtos.stream()
                .map(subjectMapper::mapToEntity)
                .map(subjectService::add)
                .collect(Collectors.toSet());

        Purpose purpose = purposeService.updateSubjects(subjectsFromDB, purposeId);


        return new ResponseEntity<>(
                purpose.getSubjects().stream()
                        .map(subjectMapper::mapToDto)
                        .collect(Collectors.toSet()),
                HttpStatus.OK);
    }
}
