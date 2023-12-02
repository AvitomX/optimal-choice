package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.papapers.optimalchoice.domain.Result;
import ru.papapers.optimalchoice.model.Purpose;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ResultService {

    private final PurposeService purposeService;

    @Autowired
    public ResultService(PurposeService purposeService) {
        this.purposeService = purposeService;
    }

    public Result compute(UUID purposeId) {
        Purpose purpose = purposeService.getOne(purposeId);

        List<Object> errors = purposeService.check(purpose);

        return Result.builder()
                .errors(errors)
                .build();
    }

}
