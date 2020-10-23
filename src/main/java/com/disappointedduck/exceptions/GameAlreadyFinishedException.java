package com.disappointedduck.exceptions;

public class GameAlreadyFinishedException extends RuntimeException {
    public GameAlreadyFinishedException(String message) {
        super("Это событие уже закончилось: " + message);
    }

    public GameAlreadyFinishedException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameAlreadyFinishedException() {
        super("Такое событие уже законичлось");
    }
}
