package ru.papapers.optimalchoice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.papapers.optimalchoice.model.Criterion;
import ru.papapers.optimalchoice.model.Purpose;
import ru.papapers.optimalchoice.model.Subject;
import ru.papapers.optimalchoice.model.SubjectRelation;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubjectRelationRepository extends JpaRepository<SubjectRelation, UUID> {

    List<SubjectRelation> findByPurposeAndCriterionAndSubjectAndComparingSubject(Purpose purpose,
                                                                                 Criterion criterion,
                                                                                 Subject subject,
                                                                                 Subject comparingSubject);
}
