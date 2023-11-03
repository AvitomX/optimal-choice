package ru.papapers.optimalchoice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.papapers.optimalchoice.model.CriterionRelation;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CriterionRelationRepository extends JpaRepository<CriterionRelation, UUID> {

    @Override
    @EntityGraph(attributePaths = { "comparingCriterion, criterion" })
    Optional<CriterionRelation> findById(UUID uuid);

}
