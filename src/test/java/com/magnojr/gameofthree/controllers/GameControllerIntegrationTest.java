package com.magnojr.gameofthree.controllers;


import com.magnojr.gameofthree.domain.Game;
import com.magnojr.gameofthree.dto.InfoMessageDTO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerIntegrationTest {
    @Value("${local.server.port}")
    private int port;
    private String URL;


    private static final String SUBSCRIBE_QUEUE_INFO = "/user/queue/info";
    private static final String SUBSCRIBE_QUEUE_GAME_DATA = "/user/queue/game-data";
    private static final String APP_NEW_GAME_ENDPOINT = "/app/new-game";

    @BeforeEach
    void setup() {
        URL = "ws://localhost:" + port + "/connect";
    }

    @Test
    void itShouldInformThePlayerToWaitForOpponentWhenThePlayer1EnterHisData() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());


        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get();


        CompletableFuture<String> futureWaitNextPlayerMessage = new CompletableFuture<>();
        stompSession.subscribe(SUBSCRIBE_QUEUE_INFO, new createStompFrameHandlerForInfoQueue(List.of(futureWaitNextPlayerMessage)));

        stompSession.send(APP_NEW_GAME_ENDPOINT, "new player 1");

        MatcherAssert.assertThat(futureWaitNextPlayerMessage.get(5, TimeUnit.SECONDS), Matchers.equalTo("wait for the next player..."));

    }

    @Test
    void itShouldCreateANewGameWhenThePlayer2EnterHisData() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());


        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get();

        CompletableFuture<Game> futureGame = new CompletableFuture<>();
        stompSession.subscribe(SUBSCRIBE_QUEUE_GAME_DATA, new createStompFrameHandlerForGameDataQueue(List.of(futureGame)));

        stompSession.send(APP_NEW_GAME_ENDPOINT, "new player X");
        stompSession.send(APP_NEW_GAME_ENDPOINT, "new player Y");

        Game game = futureGame.get(5, TimeUnit.SECONDS);


        MatcherAssert.assertThat(game.getPlayer1().getName(), Matchers.notNullValue());
        MatcherAssert.assertThat(game.getPlayer2().getName(), Matchers.notNullValue());
        MatcherAssert.assertThat(game.getId(), Matchers.notNullValue());
        MatcherAssert.assertThat(game.getNumber(), Matchers.equalTo(0));
        MatcherAssert.assertThat(game.getCurrentPlayer(), Matchers.notNullValue());

    }


    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private class createStompFrameHandlerForInfoQueue implements StompFrameHandler {

        private final List<CompletableFuture<String>> frameHandler;

        createStompFrameHandlerForInfoQueue(List<CompletableFuture<String>> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return InfoMessageDTO.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            for (CompletableFuture<String> cf : frameHandler) {
                if (!cf.isDone()) {
                    cf.complete(((InfoMessageDTO) payload).getMessage());
                }
            }
        }

    }

    private class createStompFrameHandlerForGameDataQueue implements StompFrameHandler {

        private final List<CompletableFuture<Game>> frameHandler;

        createStompFrameHandlerForGameDataQueue(List<CompletableFuture<Game>> frameHandler) {
            this.frameHandler = frameHandler;
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return Game.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            for (CompletableFuture<Game> cf : frameHandler) {
                if (!cf.isDone()) {
                    cf.complete(((Game) payload));
                }
            }
        }

    }
}

