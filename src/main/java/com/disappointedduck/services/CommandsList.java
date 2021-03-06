package com.disappointedduck.services;

import com.disappointedduck.dto.GameEventDto;
import com.disappointedduck.exceptions.GameAlreadyCreatedException;
import com.disappointedduck.exceptions.GameIsNotModeratedException;
import com.disappointedduck.exceptions.ServerRoleNotFoundException;
import com.disappointedduck.exceptions.ServerRoleNotPermittedException;
import com.disappointedduck.model.Commands;
import com.disappointedduck.model.EventHolder;
import com.disappointedduck.model.GameEvent;
import com.disappointedduck.model.HardGameEvent;
import com.disappointedduck.model.ServerRoleEnum;
import com.disappointedduck.model.WeekEnum;
import com.disappointedduck.utility.CommonProperties;
import com.disappointedduck.utility.CommonUtilities;
import com.disappointedduck.utility.MessageTextParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommandsList {
    private final EventHolder currentEvents;
    private final ObjectMapper objectMapper;

    public void showList(MessageReceivedEvent event) {
        Commands.getValues().forEach(c -> event.getMessage().getChannel().sendMessage(c).queue());
    }

    public void createGame(MessageReceivedEvent event) throws JsonProcessingException {

        Message message = event.getMessage();
        String formattedText = message.getContentDisplay().replace("!create !name", "");
        Map<String, Object> map = MessageTextParser.parseCreateMessage(formattedText);
        if (currentEvents.nameDuplicated((String) map.get("name"))) {
            throw new GameAlreadyCreatedException("Такой ивент уже начат. Выберите другое имя");
        }

        GameEvent game = GameEvent.builder()
                .name((String) map.get("name"))
                .startTime((LocalDateTime) map.get("startDateTime"))
                .playersConfig((Integer[]) map.get("people"))
                .text((String) map.get("text"))
                .players(new ArrayList<>())
                .schedule((List<WeekEnum>) map.get("schedule"))
                .mentionedRole(ServerRoleEnum.getByTitle((String) map.get("mention")))
                .build();

        MessageAction result = CommonProperties.GUILD.getTextChannelById(CommonProperties.CHANNEL).sendMessage(game.getGameInfoWithMention());
        CommonUtilities.addWhiteCheckmark(message);
        result.queue(msg -> {
            game.setMessage(msg);
            CommonUtilities.addFire(msg);
            CommonUtilities.addGreenHeart(msg);
            CommonUtilities.addShield(msg);
            currentEvents.addEvent(game);
            GameEventDto gameEventDto = new GameEventDto(game);
            try {
                MessageAction store = CommonProperties.GUILD.getTextChannelById(CommonProperties.STORE).sendMessage("<game-event>" + objectMapper.writeValueAsString(gameEventDto));
                store.queue(game::setStoredMessage);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

    }

    public void showGames(MessageReceivedEvent event) {
        List<String> infos = currentEvents.getGamesInfo();
        if (infos.size() == 0) {
            event.getMessage().getChannel().sendMessage("Тут пока пусто").queue();
        }
        event.getMessage().getChannel().sendMessage("Ивенты: " + infos
                .stream()
                .reduce((result, info) -> result + info + "\n/////////////////////\n").get()).queue();
    }

    public void createHardGame(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String formattedText = message.getContentDisplay().replace("!create-hard !name", "");
        Map<String, Object> map = MessageTextParser.parseCreateMessage(formattedText);
        if (currentEvents.nameDuplicated((String) map.get("name"))) {
            CommonUtilities.addRedCross(message);
            event.getMessage().getChannel().sendMessage("Такой ивент уже начат. Выберите другое имя").queue();
            return;
        }

        HardGameEvent hardGameEvent = HardGameEvent.builder()
                .requests(new ArrayList<>())
                .name((String) map.get("name"))
                .startTime((LocalDateTime) map.get("startDateTime"))
                .playersConfig((Integer[]) map.get("people"))
                .text((String) map.get("text"))
                .players(new ArrayList<>())
                .orgId(event.getAuthor().getId())
                .schedule((List<WeekEnum>) map.get("schedule"))
                .mentionedRole(ServerRoleEnum.getByTitle((String) map.get("mention")))
                .build();

        MessageAction result = CommonProperties.GUILD.getTextChannelById(CommonProperties.CHANNEL).sendMessage(hardGameEvent.getGameInfoWithMention());
        CommonUtilities.addWhiteCheckmark(message);
        result.queue(msg -> {
            hardGameEvent.setMessage(msg);
            currentEvents.addEvent(hardGameEvent);
            GameEventDto gameEventDto = new GameEventDto(hardGameEvent);
            try {
                MessageAction store = CommonProperties.GUILD.getTextChannelById(CommonProperties.STORE).sendMessage("<hard-game-event>" + objectMapper.writeValueAsString(gameEventDto));
                store.queue(hardGameEvent::setStoredMessage);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    public void createGameRequest(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String formattedText = message.getContentDisplay().replace("!sign !name", "");
        Map<String, String> map = MessageTextParser.parseSignMessage(formattedText);
        try {
            HardGameEvent gameEvent = (HardGameEvent) currentEvents.findByName(map.get("name"));
            gameEvent.addRequest(message);
            CommonUtilities.addEyes(message);
            gameEvent.getStoredMessage().editMessage("<hard-game-event>" + objectMapper.writeValueAsString(new GameEventDto(gameEvent))).queue();
        } catch (ClassCastException | JsonProcessingException e) {
            throw new GameIsNotModeratedException();
        }
    }

    public void addOrRemoveUserRole(Message message) {
        String text = message.getContentDisplay().replace("!role", "");
        if (text.contains("!add")) {
            String roleName = text.replace("!add", "").trim();
            ServerRoleEnum role = ServerRoleEnum.getByTitle(roleName);
            if (!role.isPermittedForBot()) {
                throw new ServerRoleNotPermittedException();
            }
            if (role == ServerRoleEnum.INACTIVE) {
                List<ServerRoleEnum> roles = Arrays.stream(ServerRoleEnum.values()).filter(ServerRoleEnum::isPermittedForBot).collect(Collectors.toList());
                roles.forEach(r -> CommonProperties.GUILD.removeRoleFromMember(message.getAuthor().getId(), CommonProperties.GUILD.getRoleById(r.getId())).queue());
                CommonProperties.GUILD.getTextChannelById("767530219921735690").sendMessage(message.getMember().getEffectiveName() + "ушел в инактив").queue();
            }
            CommonProperties.GUILD.addRoleToMember(message.getAuthor().getId(), CommonProperties.GUILD.getRoleById(role.getId())).queue();
            CommonUtilities.addWhiteCheckmark(message);
        } else if (text.contains("!remove")) {
            String roleName = text.replace("!remove", "").trim();
            ServerRoleEnum role = ServerRoleEnum.getByTitle(roleName);
            if (!role.isPermittedForBot()) {
                throw new ServerRoleNotPermittedException();
            }
            if (role == ServerRoleEnum.INACTIVE) {
                CommonProperties.GUILD.addRoleToMember(message.getAuthor().getId(), CommonProperties.GUILD.getRoleById("767462301002760234")).queue();
                CommonProperties.GUILD.getTextChannelById("767530219921735690").sendMessage(message.getMember().getEffectiveName() + "вернулся из инактива").queue();
            }
            CommonProperties.GUILD.removeRoleFromMember(message.getAuthor().getId(), CommonProperties.GUILD.getRoleById(role.getId())).queue();
            CommonUtilities.addWhiteCheckmark(message);
        } else throw new ServerRoleNotFoundException();
    }

    public void aboutMe(Message message) {
        Set<String> names = currentEvents.getNamesByPlayerId(message.getAuthor().getId());
        if (names.size() == 0) {
            message.getChannel().sendMessage("Записей нет").queue();
            return;
        }
        message.getChannel().sendMessage(names.stream().reduce((result, name) -> result += name + "\n").orElse("Ошибко T_T")).queue();
    }

    public void tryDeletingEvent(MessageDeleteEvent event) {
        currentEvents.deleteGameByMessageId(event.getMessageId());
    }
}
