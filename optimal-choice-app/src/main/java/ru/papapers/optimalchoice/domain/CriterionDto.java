package ru.papapers.optimalchoice.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
public class CriterionDto {

    private UUID id;

    @NotBlank
    private String name;

}
