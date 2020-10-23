package com.disappointedduck.model;

import com.disappointedduck.dto.GameEventDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.Message;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class HardGameEvent extends GameEvent {

    private final static String GAME_INFO_TEXT = "** Название события: %s **\n" +
            "*Время начала:* __%s__ \n" +
            "*Записаны:*  %s\n" +
            "*Свободных мест:* __%s__ \n" +
            "*%s*";

    private final static String GAME_STARTED = "%s началась! Участвуют: %s";

    private List<Message> requests;
    private String orgId;

    public HardGameEvent(GameEventDto gameEventDto, Message message) {
        super(gameEventDto, message);
        this.requests = new ArrayList<>();
        this.orgId = gameEventDto.getOrgId();
    }

    public void addRequest(Message request) {
        requests.add(request);
    }

    public String getGameInfo() {
        System.out.println("Delay calculated: " + calculateDelay());
        return String.format(GAME_INFO_TEXT, name, startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), showSigned(), calculateFreePlaces(), text);
    }

    public String getGameInfoWithMention() {
        System.out.println("Delay calculated: " + calculateDelay());
        return String.format(mentionRole() + GAME_INFO_TEXT + "\n Чтобы записаться, пользуйтесь командой \n !sign !name %s !role <Танк/ДД/Хил> !short-info <Краткое инфо о персонаже - ilvl, класс, спек, опыт (опыт есть/опыта нет)> !text <Расширенное инфо о персонаже>",
                name, startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), mentionSigned(), calculateFreePlaces(), text, name);
    }

    public String showSigned() {
        return players.stream().map(player -> "\n-" + player.getName() + " " + player.getRole().getTitle() + " " + player.getRole().getEmote()).reduce((result, s) -> result + s).orElse("");
    }

    public String mentionSigned() {
        return players.stream().map(player -> "\n-<@" + player.getId() + ">" + " " + player.getRole().getTitle() + " " + player.getRole().getEmote() + " " + player.getExtendedInfo()).reduce((result, s) -> result + s).orElse("");
    }
}
