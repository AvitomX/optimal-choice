package ru.papapers.optimalchoice.domain.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
abstract class BaseError {

    protected String code;

    protected String message;

}
