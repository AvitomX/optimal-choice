package ru.papapers.optimalchoice.mapper;

import org.mapstruct.Mapper;
import ru.papapers.optimalchoice.domain.CriterionRelationDto;
import ru.papapers.optimalchoice.model.CriterionRelation;

@Mapper(componentModel = "spring")
public interface CriterionRelationMapper {

    CriterionRelation mapToEntity(CriterionRelationDto dto);

    CriterionRelationDto mapToDto(CriterionRelation entity);
}
