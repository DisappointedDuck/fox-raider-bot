package com.disappointedduck.services;

import com.disappointedduck.listeners.MessageListener;
import com.disappointedduck.listeners.ReactionListener;
import com.disappointedduck.model.EventHolder;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

@Service
public class BotBuilder {
    @Autowired
    MessageListener messageListener;
    @Autowired
    ReactionListener reactionsListener;
    @Autowired
    EventHolder eventHolder;

    @Value("${token}")
    String token;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.addEventListeners(messageListener);
        builder.addEventListeners(reactionsListener);
        builder.build();
    }
}
