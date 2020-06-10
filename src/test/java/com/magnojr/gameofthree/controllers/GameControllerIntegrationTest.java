package com.magnojr.gameofthree.controllers;


import com.magnojr.gameofthree.services.UserMessageService;
import jdk.jshell.JShell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerIntegrationTest {
    @Value("${local.server.port}")
    private int port;
    private String URL;


    public static final String SUBSCRIBE_QUEUE = "/user/queue/info";
    private static final String APP_NEW_GAME_ENDPOINT = "/app/new-game";

    @BeforeEach
    public void setup() {
        URL = "ws://localhost:" + port + "/connect";
    }

    @Test
    public void itShouldCreateANewGameWhenAUserEnterHisData() throws InterruptedException, ExecutionException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());


        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get();


        CompletableFuture<String> cf = new CompletableFuture<>();
        StompSession.Subscription s = stompSession.subscribe(SUBSCRIBE_QUEUE, new CreateStompFrameHandler(cf));



        stompSession.send(APP_NEW_GAME_ENDPOINT, "new player 1");



    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private class CreateStompFrameHandler implements StompFrameHandler {

        private final CompletableFuture<String> frameHandler;

        public CreateStompFrameHandler(CompletableFuture<String> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            System.out.println("veio aqui");
            frameHandler.complete(payload.toString());
        }
    }
}

