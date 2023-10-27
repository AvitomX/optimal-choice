package ru.papapers.optimalchoice.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CriterionDto {

    private UUID id;

    private String name;

}
