package ru.papapers.optimalchoice.mapper;

import org.mapstruct.Mapper;
import ru.papapers.optimalchoice.domain.SubjectDto;
import ru.papapers.optimalchoice.model.Subject;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    Subject mapToEntity(SubjectDto dto);
    SubjectDto mapToDto(Subject entity);
}
