package com.disappointedduck.model;

import com.disappointedduck.exceptions.ParamInvalidException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum WeekEnum {
    MONDAY(List.of("пн", "понедельник", "mon", "monday"), DayOfWeek.MONDAY),
    TUESDAY(List.of("вт", "вторник", "tue", "tuesday"), DayOfWeek.TUESDAY),
    WEDNESDAY(List.of("ср", "среда", "wed", "wednesday"), DayOfWeek.WEDNESDAY),
    THURSDAY(List.of("чт", "четверг", "thu", "thursday"), DayOfWeek.THURSDAY),
    FRIDAY(List.of("пт", "пятница", "fri", "friday"), DayOfWeek.FRIDAY),
    SATURDAY(List.of("сб", "суббота", "sat", "saturday"), DayOfWeek.SATURDAY),
    SUNDAY(List.of("вс", "воскресенье", "sun", "sunday"), DayOfWeek.SUNDAY);

    private final List<String> titles;
    private final DayOfWeek dayOfWeek;

    public static WeekEnum fromTitle(String s) {
        return Arrays.stream(values())
                .filter(v -> v.titles.contains(s))
                .findFirst().orElseThrow(() -> new ParamInvalidException(s));
    }

    public static List<WeekEnum> fromTitle(List<String> s) {
        return s.stream().filter(string -> !string.isEmpty()).map(WeekEnum::fromTitle).collect(Collectors.toList());
    }
}
