package com.HNG14.task1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(value = {CustomNotFoundException.class})
    
    public ResponseEntity<Object> handleCustomNotFoundException(
        CustomNotFoundException customNotFoundException)
    {
        CustomException customException = new CustomException(
        customNotFoundException.getMessage(),
        customNotFoundException.getCause(),
        HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(customException,HttpStatus.NOT_FOUND);
    }
}
