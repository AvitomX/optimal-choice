package ru.papapers.optimalchoice.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class SubjectRelationDto {

    private UUID id;

    private CriterionDto criterion;

    private SubjectDto subject;

    private SubjectDto comparingSubject;

    private String value;

}
