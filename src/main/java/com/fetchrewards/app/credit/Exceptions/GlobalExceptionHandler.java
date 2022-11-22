package com.fetchrewards.app.credit.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(value = ExcessSpendPointsException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleExcessSpendPointsException(ExcessSpendPointsException ex){
        ErrorResponse apiError = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex);
        return apiError;
    }

    @ExceptionHandler(value = ValidationFailedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationFailedException(ValidationFailedException ex){
        ErrorResponse apiError = new ErrorResponse(HttpStatus.BAD_REQUEST, ex);
        return apiError;
    }

}

