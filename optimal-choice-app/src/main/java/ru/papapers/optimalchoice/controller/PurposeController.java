package ru.papapers.optimalchoice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.papapers.optimalchoice.api.domain.Purpose;

@Slf4j
@RestController
@RequestMapping(path = "/purpose", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class PurposeController {

    @PostMapping
    public ResponseEntity create(@RequestBody Purpose purpose) {
        log.info(purpose.toString());

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Purpose> get(@PathVariable String id) {
        Purpose purpose = new Purpose();

        return new ResponseEntity<Purpose>(purpose, HttpStatus.OK);
    }
}
