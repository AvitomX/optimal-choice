package ru.papapers.optimalchoice.mapper;

import org.mapstruct.Mapper;
import ru.papapers.optimalchoice.api.domain.PurposeDto;
import ru.papapers.optimalchoice.model.Purpose;

@Mapper(componentModel = "spring")
public interface PurposeMapper {

    Purpose mapToEntity(PurposeDto dto);

    PurposeDto mapToDto(Purpose entity);
}
