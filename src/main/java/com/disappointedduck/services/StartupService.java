package com.disappointedduck.services;

import com.disappointedduck.dto.GameEventDto;
import com.disappointedduck.model.EventHolder;
import com.disappointedduck.model.GameEvent;
import com.disappointedduck.model.HardGameEvent;
import com.disappointedduck.utility.CommonProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StartupService {
    private final EventHolder eventHolder;
    private final ObjectMapper objectMapper;

    public void bootGameEvent(Message msg) throws JsonProcessingException {
        String text = msg.getContentDisplay().replaceFirst("<game-event>", "");
        GameEventDto gameEventDto = objectMapper.readValue(text, GameEventDto.class);
        Message gameMessage = CommonProperties.GUILD.getTextChannelById(gameEventDto.getChannelId()).retrieveMessageById(gameEventDto.getMessageId()).complete();
        GameEvent gameEvent = new GameEvent(gameEventDto, gameMessage);
        gameEvent.setStoredMessage(msg);
        eventHolder.addEvent(gameEvent);
    }

    public void bootHardGameEvent(Message msg) throws JsonProcessingException {
        String text = msg.getContentDisplay().replaceFirst("<hard-game-event>", "");
        GameEventDto gameEventDto = objectMapper.readValue(text, GameEventDto.class);
        Message gameMessage = msg.getGuild().getTextChannelById(gameEventDto.getChannelId()).retrieveMessageById(gameEventDto.getMessageId()).complete();
        HardGameEvent gameEvent = new HardGameEvent(gameEventDto, gameMessage);
        gameEventDto.getRequests().forEach((channel, id) -> {
            gameEvent.getRequests().add(msg.getGuild().getTextChannelById(channel).retrieveMessageById(id).complete());
        });
        gameEvent.setStoredMessage(msg);
        eventHolder.addEvent(gameEvent);
    }
}
