package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.papapers.optimalchoice.domain.PurposeDto;
import ru.papapers.optimalchoice.mapper.PurposeMapper;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.repository.PurposeRepository;

import javax.transaction.Transactional;
import java.util.UUID;

@Slf4j
@Service
public class PurposeService {

    private final PurposeRepository purposeRepository;
    private final PurposeMapper purposeMapper;

    @Autowired
    public PurposeService(PurposeRepository purposeRepository,
                          PurposeMapper purposeMapper) {
        this.purposeRepository = purposeRepository;
        this.purposeMapper = purposeMapper;
    }

    @Transactional
    public Purpose create(PurposeDto purposeDto) {
        log.info("Create \"{}\" purpose and save it into DB.", purposeDto.getName());
        Purpose purpose = purposeMapper.dtoToPurpose(purposeDto);
        purpose.getCriterionRelations().forEach(criterionRelation -> criterionRelation.setPurpose(purpose));

        return purposeRepository.save(purpose);
    }

    public Purpose getOne(UUID id) {
        log.info("Get purpose by id: {}.", id);
        return purposeRepository.getById(id);
    }
}
