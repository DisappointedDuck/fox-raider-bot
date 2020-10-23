package com.disappointedduck.model;

import com.disappointedduck.exceptions.ServerRoleNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum ServerRoleEnum {
    COMMON("Лис", "767116667535884338", false),
    INACTIVE("Инактив", "767461858361606155", true),
    NOTIFICATIONS("Уведомления от бота", "767462301002760234", true),
    PVE("Пве", "767461823908806657", true),
    STATIC("Статик", "767462100212908032", true),
    PVP("Пвп", "767461729675902996", true),
    NONE("", "", false);

    private final String title;
    private final String id;
    private final boolean permittedForBot;

    public static ServerRoleEnum getByTitle(String title) {
        return Arrays.stream(values()).filter(r -> r.title.equalsIgnoreCase(title)).findFirst().orElseThrow(ServerRoleNotFoundException::new);
    }
}
