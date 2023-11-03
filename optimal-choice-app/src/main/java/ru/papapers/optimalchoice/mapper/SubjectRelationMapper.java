package ru.papapers.optimalchoice.mapper;

import org.mapstruct.Mapper;
import ru.papapers.optimalchoice.domain.SubjectRelationDto;
import ru.papapers.optimalchoice.model.SubjectRelation;

@Mapper(componentModel = "spring")
public interface SubjectRelationMapper {

    SubjectRelation mapToEntity(SubjectRelationDto dto);

    SubjectRelationDto mapToDto(SubjectRelation entity);
}
