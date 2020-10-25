package com.disappointedduck.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Data
public class EventHolder {
    private static Map<String, GameEvent> gameEvents = new HashMap<>();

    @Autowired
    ObjectMapper objectMapper;

    public void addEvent(GameEvent gameEvent) {
        gameEvents.put(gameEvent.getMessage().getId(), gameEvent);
    }

    public void removeEventByName(String name) {
        gameEvents.remove(gameEvents.values().stream().filter(e -> e.getName().equals(name)).findFirst().orElseThrow(RuntimeException::new).getMessage().getId());
    }

    public void removeEventByMessageId(String id) {
        GameEvent gameEvent = gameEvents.get(id);
        gameEvent.getStoredMessage().delete().queue();
        gameEvents.remove(id);
    }

    public List<GameEvent> findAllReady() {
        return gameEvents.values().stream().filter(e -> e.calculateDelay() < 60L).collect(Collectors.toList());
    }

    public boolean nameDuplicated(String name) {
        return gameEvents.values().stream().anyMatch(gameEvent -> gameEvent.getName().equals(name));
    }

    public List<String> getGamesInfo() {
        return gameEvents.values().stream().map(GameEvent::getGameInfo).collect(Collectors.toUnmodifiableList());
    }

    public int listSize() {
        return gameEvents.size();
    }

    public GameEvent findByMessageId(String id) {
        return gameEvents.get(id);
    }

    public GameEvent findByName(String name) {
        return gameEvents.values().stream().filter(e -> e.getName().equals(name)).findFirst().orElseThrow(RuntimeException::new);
    }

    public List<String> getNames() {
        return gameEvents.values().stream().map(GameEvent::getName).collect(Collectors.toUnmodifiableList());
    }

    public HardGameEvent findByRequestMessageId(String id) {
        return (HardGameEvent) gameEvents.values().stream()
                .filter(gameEvent -> gameEvent instanceof HardGameEvent)
                .filter(gameEvent -> ((HardGameEvent) gameEvent).getRequests().stream().anyMatch(r -> r.getId().equals(id))).findFirst().orElseThrow(RuntimeException::new);
    }

    public void deleteGameByMessageId(String id) {
        GameEvent gameEvent = gameEvents.get(id);
        gameEvent.getStoredMessage().delete().queue();
        gameEvents.remove(id);
    }

    public Set<String> getNamesByPlayerId(String id) {
        return gameEvents.values().stream()
                .filter(g -> g.getPlayers().stream().anyMatch(p -> p.getId().equals(id)))
                .map(GameEvent::getName)
                .collect(Collectors.toSet());
    }

    public boolean messageHasEvents(String id) {
        return gameEvents.containsKey(id);
    }

    public List<GameEvent> findAllByScheduleDay(DayOfWeek today) {
        return gameEvents.values().stream()
                .filter(e -> e.getSchedule().stream().anyMatch(d -> d.getDayOfWeek().equals(today))
                && e.getStartTime().getDayOfMonth() == LocalDate.now().getDayOfMonth())
                .collect(Collectors.toList());
    }

    public int getSize() {
        return gameEvents.size();
    }
}
