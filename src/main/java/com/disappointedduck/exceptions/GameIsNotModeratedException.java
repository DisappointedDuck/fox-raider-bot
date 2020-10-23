package com.disappointedduck.exceptions;

public class GameIsNotModeratedException extends RuntimeException {
    public GameIsNotModeratedException() {
        super("Вы указали название игры, созданной без модерации. К таким играм можно присоединяться смайликами." +
                " Если хотите создать игру с модерацией, воспользуйтесь !create-hard");
    }

    public GameIsNotModeratedException(String message, Throwable cause) {
        super(message, cause);
    }
}
