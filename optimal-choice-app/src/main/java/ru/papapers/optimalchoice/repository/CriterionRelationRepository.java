package ru.papapers.optimalchoice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.papapers.optimalchoice.model.Criterion;
import ru.papapers.optimalchoice.model.CriterionRelation;

import java.util.UUID;

public interface CriterionRelationRepository extends JpaRepository<CriterionRelation, UUID> {

}
