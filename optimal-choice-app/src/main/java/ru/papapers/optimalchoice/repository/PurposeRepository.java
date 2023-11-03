package ru.papapers.optimalchoice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.papapers.optimalchoice.model.Purpose;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurposeRepository extends JpaRepository<Purpose, UUID> {
    @Override
    @EntityGraph(value = "purpose-entity-graph")
    Optional<Purpose> findById(UUID uuid);
}
