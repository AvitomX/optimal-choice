package ru.papapers.optimalchoice.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.UUID;

@Data
public class CriterionDto implements Serializable {

    private UUID id;

    @NotBlank
    private String name;

}
