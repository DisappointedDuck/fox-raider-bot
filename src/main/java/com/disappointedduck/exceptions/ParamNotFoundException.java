package com.disappointedduck.exceptions;

public class ParamNotFoundException extends RuntimeException {
    public ParamNotFoundException(String message) {
        super("Не найден обязательный параметр: " + message);
    }

    public ParamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
