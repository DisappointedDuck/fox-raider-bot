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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StartupService {
    @Value("${channel}")
    public String CHANNEL;
    @Value("${store}")
    public String STORE;

    private static boolean initialized = false;
    private final EventHolder eventHolder;
    private final ObjectMapper objectMapper;

    public void botStartup(Message message) throws JsonProcessingException {
        CommonProperties.CHANNEL = CHANNEL;
        CommonProperties.STORE = STORE;
        List<Message> history = message.getGuild().getTextChannelById(CommonProperties.STORE).getHistory().retrievePast(20).complete();
        for (Message msg : history) {
            String text = msg.getContentDisplay();
            GameEventDto gameEventDto;
            GameEvent gameEvent;
            if (text.startsWith("<game-event>")) {
                text = text.replaceFirst("<game-event>", "");
                gameEventDto = objectMapper.readValue(text, GameEventDto.class);
                Message gameMessage = message.getGuild().getTextChannelById(gameEventDto.getChannelId()).retrieveMessageById(gameEventDto.getMessageId()).complete();
                gameEvent = new GameEvent(gameEventDto, gameMessage);
            } else {
                if (text.startsWith("<hard-game-event>")) {
                    text = text.replaceFirst("<hard-game-event>", "");
                    gameEventDto = objectMapper.readValue(text, GameEventDto.class);
                    Message gameMessage = message.getGuild().getTextChannelById(gameEventDto.getChannelId()).retrieveMessageById(gameEventDto.getMessageId()).complete();
                    gameEvent = new HardGameEvent(gameEventDto, gameMessage);
                    gameEventDto.getRequests().forEach((channel, id) -> {
                        ((HardGameEvent) gameEvent).getRequests().add(message.getGuild().getTextChannelById(channel).retrieveMessageById(id).complete());
                    });
                } else throw new RuntimeException();
            }
            gameEvent.setStoredMessage(msg);
            eventHolder.addEvent(gameEvent);
            initialized = true;
        }
    }

    public boolean getInitialized() {
        return initialized;
    }
}
