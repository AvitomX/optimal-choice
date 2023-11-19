package ru.papapers.optimalchoice.mapper;

import org.mapstruct.Mapper;
import ru.papapers.optimalchoice.domain.CriterionDto;
import ru.papapers.optimalchoice.model.Criterion;

@Mapper(componentModel = "spring")
public interface CriterionMapper {
    Criterion mapToEntity(CriterionDto dto);

    CriterionDto mapToDto(Criterion entity);
}
