package com.magnojr.gameofthree.config;

import com.magnojr.gameofthree.services.GameService;
import com.magnojr.gameofthree.services.UserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.LinkedList;


@Configuration
public class GameServiceConfig {

    @Autowired
    UserMessageService userMessageService;

    @Bean
    @Scope("singleton")
    public GameService gameServiceSingleton() {
        return new GameService(userMessageService, new LinkedList<>(), new HashMap<>());
    }

}
