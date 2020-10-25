package com.disappointedduck.utility;

import com.disappointedduck.exceptions.ParamNotFoundException;
import com.disappointedduck.model.WeekEnum;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class MessageTextParser {
    private List<String> paramsList = new ArrayList<>();

    public Map<String, Object> parseCreateMessage(String messageText) {
        paramsList = Arrays.asList(messageText.split(" !", 5));

        paramsList = paramsList.stream().map(String::trim).collect(Collectors.toList());

        String gameName = paramsList.get(0);

        Optional<String> stringDate = getByTextCommand("date");
        Optional<String> stringTime = getByTextCommand("time");

        LocalDateTime startDateTime = LocalDateTime.parse(stringDate.orElseThrow(() -> new ParamNotFoundException("date")).trim()
                        + " " + stringTime.orElseThrow(() -> new ParamNotFoundException("time")).trim(),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        Optional<String> stringPeople = getByTextCommand("people");
        String preparedPeople = stringPeople.orElseThrow(() -> new ParamNotFoundException("people"));
        Integer[] people = Arrays.stream(preparedPeople.trim().split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
        List<String> rawSchedule = Arrays.asList(getByTextCommand("schedule").orElse("").split(";"));
        List<WeekEnum> schedule = WeekEnum.fromTitle(rawSchedule);
        String role = getByTextCommand("mention").orElse("");
        String text = getByTextCommand("text").orElse("");

        Map<String, Object> result = new HashMap<>();
        result.put("name", gameName);
        result.put("startDateTime", startDateTime);
        result.put("people", people);
        result.put("text", text);
        result.put("mention", role);
        result.put("schedule", schedule);
        return result;
    }

    public Map<String, String> parseSignMessage(String messageText) {
        paramsList = Arrays.asList(messageText.split(" !"));
        paramsList = paramsList.stream().map(String::trim).collect(Collectors.toList());
        String gameName = paramsList.get(0);
        String roleText = getByTextCommand("role").orElseThrow(() -> new ParamNotFoundException("role"));
        String shortInfo = getByTextCommand("shortInfo").orElseThrow(() -> new ParamNotFoundException("shortInfo"));
        String longInfo = getByTextCommand("text").orElse("");

        Map<String, String> result = new HashMap<>();
        result.put("name", gameName);
        result.put("role", roleText);
        result.put("shortInfo", shortInfo);
        result.put("text", longInfo);
        return result;
    }

    private Optional<String> getByTextCommand(String textCommand) {
        return paramsList.stream().filter(s -> s.startsWith(textCommand))
                .map(s -> s.replace(textCommand, "").trim())
                .findFirst();
    }
}
