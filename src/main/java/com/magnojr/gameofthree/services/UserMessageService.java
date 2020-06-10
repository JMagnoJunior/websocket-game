package com.magnojr.gameofthree.services;

import com.magnojr.gameofthree.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMessageService {


    private final SimpMessagingTemplate template;

    public void sendWaitForYourTurnMessage(UUID userId) {
        sendInfoMessage(userId, "Wait your turn!");
    }

    public void sendWaitForOpponentMessage(UUID userId) {
        sendInfoMessage(userId, "wait for the next player...");
    }


    public void sendMoveMessage(UUID previousPlayer, Game game) {
        sendInfoMessage(previousPlayer.toString(), "wait for the other player");
        sendInfoMessage(game.getCurrentPlayer().getId(), "It is your turn!");
        sendGameData(game);
    }

    public void sendCreateNewGameMessage(Game game) {
        sendInfoMessage(game.getPlayer1().getId(), "Game Created. Your opponent is: " + game.getPlayer2().getName());
        sendInfoMessage(game.getPlayer2().getId(), "Game Created. Your opponent is: " + game.getPlayer1().getName());
        sendGameData(game);
    }

    public void sendStartingGameMessage(UUID starter, Game game) {
        sendInfoMessage(starter, "You have started the game. Wait for your turn");
        sendInfoMessage(game.getCurrentPlayer().getId(), "The opponent did the first move. Now it is your turn!");
        sendGameData(game);
    }

    public void sendErrorMessage(UUID playerId, String message) {
        sendInfoMessage(playerId, message);
    }

    private void sendInfoMessage(UUID userId, String message) {
        sendInfoMessage(userId.toString(), message);
    }

    private void sendInfoMessage(String userId, String message) {
        template.convertAndSendToUser(userId, "/queue/info", message);
        log.debug("sending info message to user. user: {}, message: {}", message, userId);
    }

    private void sendGameData(Game game) {
        template.convertAndSendToUser(game.getPlayer1().getId().toString(), "/queue/game-data", game);
        template.convertAndSendToUser(game.getPlayer2().getId().toString(), "/queue/game-data", game);
        log.debug("sending game data for game: {}", game.getId());
    }

}
