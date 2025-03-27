package ru.kata.spring.boot_security.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<JsonUserNotFoundException> globalExceptionHandler(EntityNotFoundException ex) {
        JsonUserNotFoundException data = new JsonUserNotFoundException();
        data.setMessage(ex.getMessage());
        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<JsonUserNotFoundException> globalExceptionHandler(Exception ex) {
        JsonUserNotFoundException data = new JsonUserNotFoundException();
        data.setMessage(ex.getMessage());
        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }
}
