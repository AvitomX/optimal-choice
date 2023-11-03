package ru.papapers.optimalchoice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.papapers.optimalchoice.model.Subject;
import ru.papapers.optimalchoice.repository.SubjectRepository;

import java.util.Optional;

@Service
@Slf4j
public class SubjectService {

    private final SubjectRepository repository;

    @Autowired
    public SubjectService(SubjectRepository repository) {
        this.repository = repository;
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
}
