package ru.papapers.optimalchoice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.papapers.optimalchoice.domain.PurposeDto;
import ru.papapers.optimalchoice.service.ResultService;

import java.util.UUID;

@Slf4j
@RestController
public class ResultController {

    private final ResultService resultService;

    @Autowired
    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping(value = "/result", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PurposeDto> getResult(@RequestParam(value = "purpose-id") UUID purposeId) {
        log.info("Result request with id: {}  was accepted.", purposeId);
        resultService.compute(purposeId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
