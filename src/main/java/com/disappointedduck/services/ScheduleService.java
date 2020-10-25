package com.disappointedduck.services;

import com.disappointedduck.model.EventHolder;
import com.disappointedduck.model.GameEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final EventHolder eventHolder;

    //every minute
    @Scheduled(fixedDelay = 60000)
    public void shootEvents() {
        if (eventHolder.getSize() == 0) {
            return;
        }
        for (GameEvent e : eventHolder.findAllReady()) {
            if (e.getSchedule().isEmpty()) {
                startEvent(e);
            }
        }
    }

    //every 24 hours
    @Scheduled(fixedDelay = 86400000)
    public void rescheduleEvents() {
        if (eventHolder.getSize() == 0) {
            return;
        }
        for (GameEvent e : eventHolder.findAllByScheduleDay(LocalDate.now().getDayOfWeek())) {
            if (!e.getSchedule().isEmpty()) {
                rescheduleEvent(e);
            }
        }
    }

    private void startEvent(GameEvent e) {
        eventHolder.removeEventByMessageId(e.getMessage().getId());
        e.sendStartMessage();
    }

    //TODO: какое-то ублюдство. Надо переписать нормально.
    private void rescheduleEvent(GameEvent e) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startHour = LocalTime.parse(e.getStartTime().toString(), formatter);
        LocalDate now = LocalDate.now();
        e.setStartTime(LocalDateTime.parse(now.toString() + startHour.toString(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        e.sendStartMessage();
    }
}

