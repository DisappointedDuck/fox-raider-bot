package com.disappointedduck.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoleEnum {
    DAMAGE_DEALER("ДПС", "\uD83D\uDD25"),
    TANK("Танк", "\uD83D\uDEE1️"),
    HEALER("Хилер", "\uD83D\uDC9A");

    private final String title;
    private final String emote;

    public static RoleEnum getByText(String text) {
        text = text.toLowerCase().trim();
        switch (text) {
            case "дд":
            case "дпс":
            case "дамаг":
            case "урон":
            case "dd":
            case "dps":
            case "damage":
                return DAMAGE_DEALER;
            case "хил":
            case "хилер":
            case "целитель":
            case "рестор":
            case "heal":
            case "healer":
                return HEALER;
            case "танк":
            case "tank":
            case "аггро":
            case "aggro":
                return TANK;
            default:
                throw new RuntimeException("Не удалось считать роль");
        }
    }
}
