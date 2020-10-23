package com.disappointedduck.exceptions;

public class ServerRoleNotFoundException extends RuntimeException {
    public ServerRoleNotFoundException(String message) {
        super("Не найдена серверная роль: " + message);
    }

    public ServerRoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerRoleNotFoundException() {
        super("Не найдена серверная роль");
    }
}
