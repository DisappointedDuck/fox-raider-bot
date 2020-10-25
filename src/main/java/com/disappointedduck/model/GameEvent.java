package com.disappointedduck.model;

import com.disappointedduck.dto.GameEventDto;
import com.disappointedduck.utility.CommonProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.Message;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GameEvent {
    private final static String GAME_INFO_TEXT = "** Название события: %s **\n" +
            "*Время начала:* __%s__ \n" +
            "*Записаны:*  %s \n" +
            "*Свободных мест:* __%s__ \n" +
            "*%s*";

    private final static String GAME_STARTED = "%s началась! Участвуют: %s";

    protected String name;
    protected String text;
    protected LocalDateTime startTime;
    protected Integer[] playersConfig;
    protected List<Player> players;
    protected Message message;
    protected ServerRoleEnum mentionedRole;
    protected Message storedMessage;
    protected List<WeekEnum> schedule;

    public GameEvent(GameEventDto gameEventDto, Message message) {
        this.name = gameEventDto.getName();
        this.text = gameEventDto.getText();
        this.startTime = gameEventDto.getStartTime();
        this.playersConfig = gameEventDto.getPlayersConfig();
        this.players = gameEventDto.getPlayers();
        this.message = message;
        this.schedule = gameEventDto.getSchedule();
        this.mentionedRole = gameEventDto.getMentionedRole();
    }

    public long calculateDelay() {
        Duration duration = Duration.between(LocalDateTime.now().plusHours(3), startTime);
        return duration.getSeconds();
    }

    public void sendCustomMessage(String text) {
        message.getChannel().sendMessage(text).queue();
    }

    public void reactWith(String emoji) {
        message.addReaction(emoji).queue();
    }

    public String getGameInfo() {
        System.out.println("Delay calculated: " + calculateDelay());
        return String.format(GAME_INFO_TEXT, name, startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), showSigned(), calculateFreePlaces(), text);
    }

    public String getGameInfoWithMention() {
        System.out.println("Delay calculated: " + calculateDelay());
        return String.format(mentionRole() + GAME_INFO_TEXT + "\n Чтобы записаться, оставляйте реакции :shield: :fire: :green_heart:", name,
                startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), mentionSigned(), calculateFreePlaces(), text);
    }

    public String showSigned() {
        return players.stream().map(player -> "\n-" + player.getName() + " " + player.getRole().getTitle() + " " + player.getRole().getEmote()).reduce((result, s) -> result + s).orElse("");
    }

    public String mentionSigned() {
        return players.stream().map(player -> "\n-<@" + player.getId() + ">" + " " + player.getRole().getTitle() + " " + player.getRole().getEmote()).reduce((result, s) -> result + s).orElse("");
    }

    protected String calculateFreePlaces() {
        StringBuilder sb = new StringBuilder();
        String tanksAmount = String.valueOf(playersConfig[0] - getAmountByRole(RoleEnum.TANK));
        String healersAmount = String.valueOf(playersConfig[1] - getAmountByRole(RoleEnum.HEALER));
        String ddAmount = String.valueOf(playersConfig[2] - getAmountByRole(RoleEnum.DAMAGE_DEALER));
        sb.append("Танки: ").append(tanksAmount).append("\n")
                .append("Хилеры: ").append(healersAmount).append("\n")
                .append("ДД: ").append(ddAmount).append("\n");

        return sb.toString();
    }

    protected long getAmountByRole(RoleEnum roleEnum) {
        return players.stream().filter(p -> p.getRole() == roleEnum).count();
    }

    protected String mentionRole() {
        return mentionedRole == ServerRoleEnum.NONE ? "" : "<@&" + mentionedRole.getId() + ">";
    }

    public void sendStartMessage() {
        CommonProperties.GUILD.getTextChannelById(CommonProperties.CHANNEL).sendMessage(String.format(GAME_STARTED, name, mentionSigned())).queue();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayerById(String id) {
        players.remove(players.stream().filter(p -> p.getId().equals(id)).findFirst().orElseThrow(RuntimeException::new));
    }

    public void updateMessage(String text) {
        message.editMessage(text).queue();
    }
}
