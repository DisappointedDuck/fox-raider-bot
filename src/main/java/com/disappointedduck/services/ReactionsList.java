package com.disappointedduck.services;

import com.disappointedduck.dto.GameEventDto;
import com.disappointedduck.model.EventHolder;
import com.disappointedduck.model.GameEvent;
import com.disappointedduck.model.HardGameEvent;
import com.disappointedduck.model.Player;
import com.disappointedduck.model.RoleEnum;
import com.disappointedduck.utility.MessageTextParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class ReactionsList {
    private final EventHolder eventHolder;

    private final ObjectMapper objectMapper;

    public void signOnGame(Message message, Player player) throws JsonProcessingException {
        if (eventHolder.listSize() == 0) {
            message.getChannel()
                    .sendMessage("Активных игр нет! Впишите !list, чтобы увидеть как создать игру")
                    .queue();
            return;
        }
        GameEvent game;

        try {
            game = eventHolder.findByMessageId(message.getId());
        } catch (RuntimeException e) {
            System.out.println(e.toString());
            message.getChannel().sendMessage("Произошла ошибка! Скорее всего игра на которую вы подписывались уже закончилась" +
                    "Список активных игр: " + eventHolder.getNames()).queue();
            return;
        }
        List<Player> players = game.getPlayers();
        if (players.stream().anyMatch(p -> p.getId().equals(player.getId()))) {
            throw new RuntimeException("Player has already signed with role");
        }
        players.add(player);
        message.editMessage(game.getGameInfoWithMention()).queue();
        game.getStoredMessage().editMessage("<game-event>" + objectMapper.writeValueAsString(new GameEventDto(game))).queue();
        message.addReaction("U+2705").queue();
    }

    @SneakyThrows
    public void unsignOnGame(Message message, String playerId) {
        if (eventHolder.listSize() == 0) {
            message.getChannel()
                    .sendMessage("Активных игр нет! Впишите !list, чтобы увидеть как создать игру")
                    .queue();
            return;
        }
        GameEvent game = eventHolder.findByMessageId(message.getId());
        game.getPlayers().remove(game.getPlayers().stream().filter(p -> p.getId().equals(playerId)).findFirst().get());
        message.editMessage(game.getGameInfoWithMention()).queue();
        game.getStoredMessage().editMessage("<game-event>" + objectMapper.writeValueAsString(new GameEventDto(game))).queue();
        message.removeReaction("U+2705").queue();
    }

    @SneakyThrows
    public void approveRequest(String messageId, String orgId) {
        HardGameEvent gameEvent = eventHolder.findByRequestMessageId(messageId);
        if (!gameEvent.getOrgId().equals(orgId)) {
            return;
        }
        Message request = gameEvent.getRequests().stream().filter(r -> r.getId().equals(messageId)).findFirst().orElseThrow(RuntimeException::new);
        Map<String, String> map = MessageTextParser.parseSignMessage(request.getContentDisplay().replace("!name ", ""));
        gameEvent.addPlayer(Player.builder()
                .id(request.getAuthor().getId())
                .name(request.getMember().getEffectiveName())
                .extendedInfo(map.get("shortInfo"))
                .role(RoleEnum.getByText(map.get("role")))
                .build());
        gameEvent.getStoredMessage().editMessage("<hard-game-event>" + objectMapper.writeValueAsString(new GameEventDto(gameEvent))).queue();
        gameEvent.updateMessage(gameEvent.getGameInfoWithMention());
    }

    @SneakyThrows
    public void disapproveRequest(String messageId, String orgId) {
        HardGameEvent gameEvent = eventHolder.findByRequestMessageId(messageId);
        if (!gameEvent.getOrgId().equals(orgId)) {
            return;
        }
        Message request = gameEvent.getRequests().stream().filter(r -> r.getId().equals(messageId)).findFirst().orElseThrow(RuntimeException::new);
        gameEvent.removePlayerById(request.getAuthor().getId());
        gameEvent.getStoredMessage().editMessage("<hard-game-event>" + objectMapper.writeValueAsString(new GameEventDto(gameEvent))).queue();
        gameEvent.updateMessage(gameEvent.getGameInfoWithMention());
    }

    public boolean messageHasNoGame(String messageId) {
        return !eventHolder.messageHasEvents(messageId);
    }
}
