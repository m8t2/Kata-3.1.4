package ru.kata.spring.boot_security.demo.exception;

public class JsonUserNotFoundException {

    private String message;

    public JsonUserNotFoundException() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
