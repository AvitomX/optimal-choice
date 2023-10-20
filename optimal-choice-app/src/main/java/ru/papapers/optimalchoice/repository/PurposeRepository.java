package ru.papapers.optimalchoice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.papapers.optimalchoice.model.Purpose;

import java.util.UUID;

public interface PurposeRepository extends JpaRepository<Purpose, UUID> {

}
