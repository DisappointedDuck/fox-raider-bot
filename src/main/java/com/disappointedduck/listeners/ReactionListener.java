package com.disappointedduck.listeners;

import com.disappointedduck.model.Player;
import com.disappointedduck.model.RoleEnum;
import com.disappointedduck.services.ReactionsList;
import com.disappointedduck.utility.CommonUtilities;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;


@RequiredArgsConstructor
@Service
@Slf4j
public class ReactionListener extends ListenerAdapter {
    private final static List<String> SIGN_EMOJI = List.of("\uD83D\uDD25", "\uD83D\uDC9A", "\uD83D\uDEE1️");
    private final ReactionsList reactionsList;

    @SneakyThrows
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        System.out.println(event.getReactionEmote().getName());
        if (event.getUser().isBot()) {
            return;
        }
        try {
            if (!reactionsList.messageHasNoGame(event.getMessageId())
                    && SIGN_EMOJI.contains(event.getReactionEmote().getName())) {
                Player player = Player.builder()
                        .id(event.getUserId())
                        .name(event.getUser().getName())
                        .role(determineRole(event.getReactionEmote().getName()))
                        .build();

                Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
                reactionsList.signOnGame(message, player);
            }
            if (event.getReactionEmote().getName().equals("✅")) {
                reactionsList.approveRequest(event.getMessageId(), event.getUserId());
            }
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
            CommonUtilities.addRedCross(event.retrieveMessage().complete());
            event.getChannel().sendMessage(e.getMessage()).queue();
        }
    }

    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (SIGN_EMOJI.contains(event.getReactionEmote().getName()) &&
                !reactionsList.messageHasNoGame(event.getMessageId())) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            reactionsList.unsignOnGame(message, event.getUserId());
            return;
        }
        if (event.getReactionEmote().getName().equals("✅")) {
            reactionsList.disapproveRequest(event.getMessageId(), event.getUserId());
        }
    }

    private RoleEnum determineRole(String emoji) {
        switch (emoji) {
            case "\uD83D\uDD25":
                return RoleEnum.DAMAGE_DEALER;
            case "\uD83D\uDC9A":
                return RoleEnum.HEALER;
            case "\uD83D\uDEE1️":
                return RoleEnum.TANK;
            default:
                throw new IllegalStateException("Unexpected value: " + emoji);
        }
    }
}
