package com.disappointedduck.utility;

import com.disappointedduck.exceptions.ParamNotFoundException;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class MessageTextParser {
    public Map<String, Object> parseCreateMessage(String messageText) {
        List<String> paramsList = Arrays.asList(messageText.split(" !", 5));

        paramsList = paramsList.stream().map(String::trim).collect(Collectors.toList());

        String gameName = paramsList.get(0);

        Optional<String> stringDate = getByTextCommand(paramsList, "date");
        Optional<String> stringTime = getByTextCommand(paramsList, "time");

        LocalDateTime startDateTime = LocalDateTime.parse(stringDate.orElseThrow(() -> new ParamNotFoundException("date")).trim()
                        + " " + stringTime.orElseThrow(() -> new ParamNotFoundException("time")).trim(),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        Optional<String> stringPeople = getByTextCommand(paramsList, "people");
        String preparedPeople = stringPeople.orElseThrow(() -> new ParamNotFoundException("people"));
        Integer[] people = Arrays.stream(preparedPeople.trim().split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
        String role = getByTextCommand(paramsList, "mention").orElse("");
        String text = getByTextCommand(paramsList, "text").orElse("");

        Map<String, Object> result = new HashMap<>();
        result.put("name", gameName);
        result.put("startDateTime", startDateTime);
        result.put("people", people);
        result.put("text", text);
        result.put("mention", role);
        return result;
    }

    public Map<String, String> parseSignMessage(String messageText) {
        List<String> paramsList = Arrays.asList(messageText.split(" !"));
        paramsList = paramsList.stream().map(String::trim).collect(Collectors.toList());
        String gameName = paramsList.get(0);
        String roleText = paramsList.get(1).replace("role ", "");
        String shortInfo = paramsList.get(2).replace("short-info ", "");
        String longInfo = paramsList.get(3).replace("text", "");

        Map<String, String> result = new HashMap<>();
        result.put("name", gameName);
        result.put("role", roleText);
        result.put("shortInfo", shortInfo);
        result.put("text", longInfo);
        return result;
    }

    private Optional<String> getByTextCommand(List<String> paramsList, String textCommand) {
        return paramsList.stream().filter(s -> s.startsWith(textCommand))
                .map(s -> s.replace(textCommand, "").trim())
                .findFirst();
    }
}
