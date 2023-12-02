package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.papapers.optimalchoice.domain.errors.ErrorCode;
import ru.papapers.optimalchoice.domain.errors.SubjectError;
import ru.papapers.optimalchoice.mapper.CriterionMapper;
import ru.papapers.optimalchoice.mapper.SubjectMapper;
import ru.papapers.optimalchoice.model.Criterion;
import ru.papapers.optimalchoice.model.Subject;
import ru.papapers.optimalchoice.repository.SubjectRepository;

import java.util.Optional;

@Service
@Slf4j
public class SubjectService {

    private final SubjectRepository repository;
    private final SubjectMapper subjectMapper;
    private final CriterionMapper criterionMapper;

    @Autowired
    public SubjectService(SubjectRepository repository,
                          SubjectMapper subjectMapper,
                          CriterionMapper criterionMapper) {
        this.repository = repository;
        this.subjectMapper = subjectMapper;
        this.criterionMapper = criterionMapper;
    }

    @Transactional
    public Subject add(Subject subject) {
        Optional<Subject> subjectOptional = repository.findByName(subject.getName());
        if (subjectOptional.isPresent()) {
            return subjectOptional.get();
        } else {
            log.info("Created new subject {}", subject.getName());
            return repository.save(subject);
        }
    }

    public SubjectError createSubjectError(Subject subject, Criterion criterion, ErrorCode errorCode) {
        SubjectError subjectError = new SubjectError(errorCode.getCode(),
                errorCode.getMessage(),
                subjectMapper.mapToDto(subject),
                criterionMapper.mapToDto(criterion));

        log.error("{}", subjectError);

        return subjectError;
    }
}
