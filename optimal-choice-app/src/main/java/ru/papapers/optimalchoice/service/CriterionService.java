package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.papapers.optimalchoice.model.Criterion;
import ru.papapers.optimalchoice.repository.CriterionRepository;

import java.util.Optional;

@Service
@Slf4j
public class CriterionService {

    private final CriterionRepository criterionRepository;

    @Autowired
    public CriterionService(CriterionRepository criterionRepository) {
        this.criterionRepository = criterionRepository;
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
}
