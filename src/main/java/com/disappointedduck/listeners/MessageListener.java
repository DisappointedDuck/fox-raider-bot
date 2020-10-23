package com.disappointedduck.listeners;

import com.disappointedduck.services.CommandsList;
import com.disappointedduck.services.StartupService;
import com.disappointedduck.utility.CommonUtilities;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import javax.annotation.Nonnull;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageListener extends ListenerAdapter {
    private final CommandsList commandsList;
    private final StartupService startupService;

    @SneakyThrows
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!startupService.getInitialized()) {
            startupService.botStartup(event.getMessage());
        }

        try {
            if (event.getAuthor().isBot()) {
                return;
            }
            String msgText = event.getMessage().getContentRaw();
            if (msgText.startsWith("!list") || msgText.startsWith("!help") || msgText.startsWith("!info")) {
                commandsList.showList(event);
                return;
            }
            if (msgText.startsWith("!create ")) {
                commandsList.createGame(event);
                return;
            }
            if (msgText.startsWith("!events")) {
                commandsList.showGames(event);
                return;
            }
            if (msgText.startsWith("!create-hard")) {
                commandsList.createHardGame(event);
                return;
            }
            if (msgText.startsWith("!sign")) {
                commandsList.createGameRequest(event);
            }
            if (msgText.startsWith("!role")) {
                commandsList.addOrRemoveUserRole(event.getMessage());
            }

        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
            CommonUtilities.addRedCross(event.getMessage());
            event.getChannel().sendMessage(e.getMessage()).queue();
        }
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        commandsList.tryDeletingEvent(event);
    }
}
