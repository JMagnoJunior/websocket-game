package com.magnojr.gameofthree.controllers;

import com.magnojr.gameofthree.dto.MoveDTO;
import com.magnojr.gameofthree.dto.StartGameMoveDTO;
import com.magnojr.gameofthree.services.GameService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.UUID;

@Controller
public class GameController {

    @Resource(name = "gameServiceSingleton")
    GameService gameService;

    @MessageMapping("/new-game")
    public void prepareGame(Principal principal, @Payload String userName) {
        UUID userId = UUID.fromString(principal.getName());
        gameService.loadingGame(userId, userName);
    }

    @MessageMapping("/next-move")
    public void executeNextMove(Principal principal, MoveDTO move) {
        UUID userId = UUID.fromString(principal.getName());
        gameService.nextMove(userId, move);
    }

    @MessageMapping("/start-game-move")
    public void startGameMove(Principal principal, StartGameMoveDTO startGameMoveDTO) {
        UUID userId = UUID.fromString(principal.getName());
        gameService.startGame(userId, startGameMoveDTO);
    }

}
