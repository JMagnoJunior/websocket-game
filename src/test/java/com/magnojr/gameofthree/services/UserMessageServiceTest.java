package com.magnojr.gameofthree.services;

import com.magnojr.gameofthree.DataProvider;
import com.magnojr.gameofthree.domain.Game;
import com.magnojr.gameofthree.dto.InfoMessageDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

class UserMessageServiceTest {


    private SimpMessagingTemplate simpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);

    private UserMessageService userMessageService = new UserMessageService(simpMessagingTemplate);

    @Test
    void itShouldSendWaitForYourTurnMessageForUsers() {

        UUID userId = UUID.randomUUID();
        String message = "Wait your turn!";

        userMessageService.sendWaitForYourTurnMessage(userId);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSendToUser(userId.toString(), "/queue/info", new InfoMessageDTO(message));
    }

    @Test
    void itShouldSendWaitingForOpponentMessage() {
        UUID userId = UUID.randomUUID();
        String message = "wait for the next player...";

        userMessageService.sendWaitForOpponentMessage(userId);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSendToUser(userId.toString(), "/queue/info", new InfoMessageDTO(message));
    }

    @Test
    void itShouldMoveMessage() {
        UUID previousUser = UUID.randomUUID();
        Game game = DataProvider.createNewGame();

        userMessageService.sendMoveMessage(previousUser, game);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSendToUser(previousUser.toString(), "/queue/info", new InfoMessageDTO("wait for the other player"));

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSendToUser(game.getCurrentPlayer().getId().toString(), "/queue/info", new InfoMessageDTO("It is your turn!"));

        verifySendGameData(game);
    }

    @Test
    void itShouldSendStartingGameMessage() {

        Game game = DataProvider.createNewGame();

        userMessageService.sendStartingGameMessage(game.getPlayer1().getId(), game);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSendToUser(game.getPlayer1().getId().toString(), "/queue/info",
                        new InfoMessageDTO("You have started the game. Wait for your turn"));

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSendToUser(game.getCurrentPlayer().getId().toString(), "/queue/info",
                        new InfoMessageDTO("The opponent did the first move. Now it is your turn!"));

        verifySendGameData(game);
    }

    @Test
    void itShouldSendCreateNewGameMessage() {
        Game game = DataProvider.createNewGame();

        userMessageService.sendCreateNewGameMessage(game);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSendToUser(game.getPlayer1().getId().toString(), "/queue/info",
                        new InfoMessageDTO("Game Created. Your opponent is: " + game.getPlayer2().getName()));

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSendToUser(game.getPlayer2().getId().toString(), "/queue/info",
                        new InfoMessageDTO("Game Created. Your opponent is: " + game.getPlayer1().getName()));

        verifySendGameData(game);

    }

    private void verifySendGameData(Game game) {
        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSendToUser(game.getPlayer1().getId().toString(), "/queue/game-data", game);

        Mockito.verify(simpMessagingTemplate, Mockito.times(1))
                .convertAndSendToUser(game.getPlayer2().getId().toString(), "/queue/game-data", game);
    }


}
