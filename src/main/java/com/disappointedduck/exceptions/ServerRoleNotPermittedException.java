package com.disappointedduck.exceptions;

public class ServerRoleNotPermittedException extends RuntimeException {
    public ServerRoleNotPermittedException(String message) {
        super("Бот не может управлять этой ролью - обратитесь к админу: " + message);
    }

    public ServerRoleNotPermittedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerRoleNotPermittedException(){
        super("Бот не может управлять этой ролью - обратитесь к админу");
    }
}
