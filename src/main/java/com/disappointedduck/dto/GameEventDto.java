package com.disappointedduck.dto;

import com.disappointedduck.model.GameEvent;
import com.disappointedduck.model.HardGameEvent;
import com.disappointedduck.model.Player;
import com.disappointedduck.model.ServerRoleEnum;
import com.disappointedduck.model.WeekEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameEventDto {
    protected String name;
    protected String text;
    protected LocalDateTime startTime;
    protected Integer[] playersConfig;
    protected List<Player> players;
    protected String messageId;
    protected String channelId;
    protected List<WeekEnum> schedule;
    protected ServerRoleEnum mentionedRole;
    protected Map<String, String> requests;
    protected String orgId;

    public GameEventDto(GameEvent gameEvent) {
        this.name = gameEvent.getName();
        this.text = gameEvent.getText();
        this.startTime = gameEvent.getStartTime();
        this.playersConfig = gameEvent.getPlayersConfig();
        this.players = gameEvent.getPlayers();
        this.messageId = gameEvent.getMessage().getId();
        this.channelId = gameEvent.getMessage().getChannel().getId();
        this.mentionedRole = gameEvent.getMentionedRole();
        this.schedule = gameEvent.getSchedule();
    }

    public GameEventDto(HardGameEvent gameEvent) {
        this.name = gameEvent.getName();
        this.text = gameEvent.getText();
        this.startTime = gameEvent.getStartTime();
        this.playersConfig = gameEvent.getPlayersConfig();
        this.players = gameEvent.getPlayers();
        this.messageId = gameEvent.getMessage().getId();
        this.channelId = gameEvent.getMessage().getChannel().getId();
        this.mentionedRole = gameEvent.getMentionedRole();
        this.orgId = gameEvent.getOrgId();
        this.requests = createMapOfRequests(gameEvent.getRequests());
        this.schedule = gameEvent.getSchedule();
    }

    private Map<String, String> createMapOfRequests(List<Message> requests) {
        Map<String, String> requestMap = new HashMap<>();
        requests.forEach(r -> requestMap.put(r.getChannel().getId(), r.getId()));
        return requestMap;
    }
}





