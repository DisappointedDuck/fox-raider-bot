package com.disappointedduck.exceptions;

public class ParamInvalidException extends RuntimeException {
    public ParamInvalidException(String message) {
        super("Параметр введен с ошибкой: " + message);
    }

    public ParamInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
