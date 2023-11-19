package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.papapers.optimalchoice.domain.PurposeDto;
import ru.papapers.optimalchoice.domain.errors.ApiError;
import ru.papapers.optimalchoice.model.CriterionRelation;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.model.SubjectRelation;
import ru.papapers.optimalchoice.repository.PurposeRepository;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PurposeService {

    private final PurposeRepository purposeRepository;
    private final CriterionRelationService criterionRelationService;
    private final SubjectRelationService subjectRelationService;

    @Autowired
    public PurposeService(PurposeRepository purposeRepository,
                          CriterionRelationService criterionRelationService,
                          SubjectRelationService subjectRelationService) {
        this.purposeRepository = purposeRepository;
        this.criterionRelationService = criterionRelationService;
        this.subjectRelationService = subjectRelationService;
    }

    @Transactional
    public Purpose create(PurposeDto purposeDto) {
        log.info("Create \"{}\" purpose and save it into DB.", purposeDto.getName());

        Purpose purpose = new Purpose();
        purpose.setName(purposeDto.getName());
        Purpose purposeFromDB = purposeRepository.save(purpose);

        List<CriterionRelation> criterionRelationsFromDB = criterionRelationService.add(purposeDto.getCriterionRelations(), purposeFromDB);
        purposeFromDB.setCriterionRelations(new HashSet<>(criterionRelationsFromDB));

        List<SubjectRelation> subjectRelationsFromDB = subjectRelationService.add(purposeDto.getSubjectRelations(), purposeFromDB);
        purposeFromDB.setSubjectRelations(new HashSet<>(subjectRelationsFromDB));

        return purposeFromDB;
    }

    @Transactional(readOnly = true)
    public Purpose getOne(UUID id) {
        log.info("Get purpose by id: {}.", id);
        return purposeRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purpose with ID = " + id + " not exists"));
    }

    public void check(Purpose purpose) {
        List<ApiError> criterionApiErrors = criterionRelationService.check(purpose.getCriterionRelations());


    }
}
