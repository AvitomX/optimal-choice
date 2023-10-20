package ru.papapers.optimalchoice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.papapers.optimalchoice.domain.PurposeDto;
import ru.papapers.optimalchoice.model.Purpose;

@Mapper(componentModel = "spring")
public interface PurposeMapper {

    Purpose dtoToPurpose(PurposeDto dto);

    PurposeDto toDto(Purpose purpose);
}
