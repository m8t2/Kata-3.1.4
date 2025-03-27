package ru.kata.spring.boot_security.demo.exception;

public class JsonUserNotFound extends RuntimeException {

    public JsonUserNotFound(String message) {
        super(message);
    }
}
