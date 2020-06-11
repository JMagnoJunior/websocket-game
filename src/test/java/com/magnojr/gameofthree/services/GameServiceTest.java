package com.magnojr.gameofthree.services;

import com.magnojr.gameofthree.DataProvider;
import com.magnojr.gameofthree.domain.Game;
import com.magnojr.gameofthree.domain.Player;
import com.magnojr.gameofthree.dto.MoveDTO;
import com.magnojr.gameofthree.dto.StartGameMoveDTO;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

class GameServiceTest {

    private UserMessageService userMessageService = Mockito.mock(UserMessageService.class);


    private final HashMap<UUID, Game> games = Mockito.mock(HashMap.class);
    private final Queue<Player> waitingList = Mockito.mock(LinkedList.class);

    private GameService gameService = new GameService(userMessageService, waitingList, games);

    @Test
    void itShouldAddUserToWaitingListWhenTheFirstUserLoadTheGame() {

        UUID userId = UUID.randomUUID();
        String userName = "Player1";

        Mockito.when(waitingList.isEmpty()).thenReturn(true);

        gameService.loadingGame(userId, userName);

        Mockito.verify(waitingList, Mockito.times(1)).add(new Player(userId, userName));
    }

    @Test
    void itShouldCreateANewGameWhenTheSecondPlayerLoadTheGame() {

        Player player1 = new Player(UUID.randomUUID(), "P1");


        Mockito.when(waitingList.isEmpty()).thenReturn(false);
        Mockito.when(waitingList.poll()).thenReturn(player1);

        gameService.loadingGame(UUID.randomUUID(), "P2");

        Mockito.verify(games, Mockito.times(1)).put(Mockito.any(), Mockito.any());

    }

    @Test
    void itShouldStartAGameWhenOnePlayerProvideTheNumber() {

        Game game = DataProvider.createNewGame();
        int startNumber = DataProvider.getRandomValue(1, Integer.MAX_VALUE);

        StartGameMoveDTO startGameMoveDTO = new StartGameMoveDTO(game.getId(), startNumber);

        Mockito.when(games.get(game.getId())).thenReturn(game);
        Mockito.when(games.containsKey(game.getId())).thenReturn(true);

        gameService.startGame(game.getPlayer1().getId(), startGameMoveDTO);

        MatcherAssert.assertThat(startNumber, equalTo(game.getNumber()));
        MatcherAssert.assertThat(game.getPlayer2(), equalTo(game.getCurrentPlayer()));
    }

    @Test
    void itShouldSendErrorMessageWhenPlayerStartAGameAlreadyStarted() {
        Game game = DataProvider.createNewGame();
        final int startNumber = DataProvider.getRandomValue(1, Integer.MAX_VALUE);
        game.startGame(game.getPlayer1().getId(), startNumber);

        StartGameMoveDTO startGameMoveDTO = new StartGameMoveDTO(game.getId(), startNumber);

        Mockito.when(games.get(game.getId())).thenReturn(game);

        gameService.startGame(game.getPlayer2().getId(), startGameMoveDTO);

        Mockito.verify(userMessageService, Mockito.times(1)).sendErrorMessage(Mockito.any(), Mockito.any());

    }

    @Test
    void itShouldUpdateTheGameWhenPlayerMove() {
        Game game = DataProvider.createNewGame();
        final int number = DataProvider.getRandomValue(1, Integer.MAX_VALUE);
        game.startGame(game.getPlayer1().getId(), number);

        MoveDTO moveDTO = new MoveDTO(game.getId());

        Mockito.when(games.get(game.getId())).thenReturn(game);

        gameService.nextMove(game.getPlayer2().getId(), moveDTO);

        MatcherAssert.assertThat(game.getNumber(), equalTo((number + 1) / 3));
        MatcherAssert.assertThat(game.getPlayer1(), equalTo(game.getCurrentPlayer()));

    }

    @Test
    void itShouldSendErrorMessageWhenTheAPlayerMakesAMoveOnAGameNotStarted() {
        Game game = DataProvider.createNewGame();

        MoveDTO moveDTO = new MoveDTO(game.getId());
        Mockito.when(games.get(game.getId())).thenReturn(game);

        gameService.nextMove(game.getPlayer1().getId(), moveDTO);

        Mockito.verify(userMessageService, Mockito.times(1)).sendErrorMessage(Mockito.any(), Mockito.any());

    }



}
