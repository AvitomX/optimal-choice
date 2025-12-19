package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.papapers.optimalchoice.api.domain.CriterionDto;
import ru.papapers.optimalchoice.api.domain.errors.CriterionError;
import ru.papapers.optimalchoice.api.domain.errors.ErrorCode;
import ru.papapers.optimalchoice.mapper.CriterionMapper;
import ru.papapers.optimalchoice.model.Criterion;
import ru.papapers.optimalchoice.repository.CriterionRepository;
import ru.papapers.optimalchoice.repository.PurposeRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CriterionService {

    private final CriterionRepository criterionRepository;
    private final CriterionMapper mapper;
    private final PurposeRepository purposeRepository;

    @Autowired
    public CriterionService(CriterionRepository criterionRepository,
                            CriterionMapper mapper,
                            PurposeRepository purposeRepository) {
        this.criterionRepository = criterionRepository;
        this.mapper = mapper;
        this.purposeRepository = purposeRepository;
    }

    @Transactional
    public Criterion add(Criterion criterion) {
        Optional<Criterion> criterionOptional = criterionRepository.findByName(criterion.getName());
        if (criterionOptional.isPresent()) {
            return criterionOptional.get();
        } else {
            log.info("Created new criterion {}", criterion.getName());
            return criterionRepository.save(criterion);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Criterion> getOneByName(String name) {
        return criterionRepository.findByName(name);
    }

    public CriterionError createCriterionError(Criterion criterion, ErrorCode errorCode) {
        CriterionError criterionError = new CriterionError(errorCode.getCode(),
                errorCode.getMessage(),
                mapper.mapToDto(criterion));

        log.error("{}", criterionError);

        return criterionError;
    }

    @Transactional
    public Set<Criterion> add(Set<CriterionDto> criteriaDto) {
        if (criteriaDto == null || criteriaDto.isEmpty()) {
            return null;
        }

        Set<Criterion> criteria = criteriaDto.stream().map(mapper::mapToEntity).collect(Collectors.toSet());
        log.info("Adding new criteria. Amount: {}", criteria.size());
        return criterionRepository.saveAll(criteria).stream().collect(Collectors.toSet());
    }

    @Transactional
    public Set<Criterion> getList(UUID purposeId) {
        return purposeRepository.findPurposeCriteriaById(purposeId)
                .orElseThrow(() -> new EntityNotFoundException("Purpose not found with id: " + purposeId))
                .getCriteria();
    }
}
