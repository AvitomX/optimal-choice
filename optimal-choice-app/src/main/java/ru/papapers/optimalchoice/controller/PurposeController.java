package ru.papapers.optimalchoice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.papapers.optimalchoice.domain.PurposeDto;
import ru.papapers.optimalchoice.mapper.PurposeMapper;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.service.PurposeService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/purpose")
public class PurposeController {
    private final PurposeService purposeService;
    private final PurposeMapper  purposeMapper;

    @Autowired
    public PurposeController(PurposeService purposeService,
                             PurposeMapper purposeMapper) {
        this.purposeService = purposeService;
        this.purposeMapper = purposeMapper;
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PurposeDto> create(@RequestBody PurposeDto purposeDto) {
        log.info("Request for new purpose creation was accepted.");
        Purpose purpose = purposeService.create(purposeDto);

        return new ResponseEntity<PurposeDto>(purposeMapper.toDto(purpose), HttpStatus.CREATED);
    }


    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PurposeDto> get(@PathVariable("id") UUID id) {
        log.info("Request for purpose with id: {} was accepted.", id);
        Purpose purpose = purposeService.getOne(id);

        return new ResponseEntity<PurposeDto>(purposeMapper.toDto(purpose), HttpStatus.OK);
    }
}
