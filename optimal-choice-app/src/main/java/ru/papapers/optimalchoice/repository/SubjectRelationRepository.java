package ru.papapers.optimalchoice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.papapers.optimalchoice.model.SubjectRelation;

import java.util.UUID;

@Repository
public interface SubjectRelationRepository extends JpaRepository<SubjectRelation, UUID> {

}
