package com.disappointedduck.services;

import com.disappointedduck.dto.GameEventDto;
import com.disappointedduck.model.GameEvent;
import com.disappointedduck.utility.CommonProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StartupController {
    private static boolean initialized = false;
    private final StartupService startupService;
    @Value("${channel}")
    private String CHANNEL;
    @Value("${store}")
    private String STORE;

    public void botStartup(Message message) {
        CommonProperties.CHANNEL = CHANNEL;
        CommonProperties.STORE = STORE;
        CommonProperties.GUILD = message.getGuild();
        List<Message> history = message.getGuild().getTextChannelById(CommonProperties.STORE).getHistory().retrievePast(100).complete();
        for (Message msg : history) {
            try {
                String text = msg.getContentDisplay();
                if (text.startsWith("<game-event>")) {
                    startupService.bootGameEvent(msg);
                    break;
                }
                if (text.startsWith("<hard-game-event>")) {
                    startupService.bootHardGameEvent(msg);
                    break;
                }
            } catch (Exception e) {
                log.error("Could not load saved game" + e);
            }
        }

        initialized = true;
    }

    public boolean getInitialized() {
        return initialized;
    }
}
