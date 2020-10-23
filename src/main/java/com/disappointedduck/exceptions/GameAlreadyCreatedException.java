package com.disappointedduck.exceptions;

public class GameAlreadyCreatedException extends RuntimeException {
    public GameAlreadyCreatedException(String message) {
        super("Событие с таким именем уже создано " + message);
    }

    public GameAlreadyCreatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
