package ru.papapers.optimalchoice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.papapers.optimalchoice.model.Subject;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID> {
    Optional<Subject> findByName(String name);

}
