package com.itrex.distance.measurer.handler;

import com.itrex.distance.measurer.exception.ErrorMessage;
import com.itrex.distance.measurer.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorMessage handleValidationError(ValidationException e) {
        return new ErrorMessage(e.getMessage());
    }
}
