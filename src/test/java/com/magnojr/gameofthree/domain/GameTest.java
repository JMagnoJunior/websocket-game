package com.magnojr.gameofthree.domain;

import com.magnojr.gameofthree.DataProvider;
import com.magnojr.gameofthree.exception.InvalidMoveException;
import com.magnojr.gameofthree.exception.InvalidStartException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GameTest {

    @Test
    void itShouldStartACreatedGameWhenReceivesAStarterUserIdAndANumber() {

        Game game = DataProvider.createNewGame();
        UUID starterUserId = game.getPlayer2().getId();
        final int number = 56;

        game.startGame(starterUserId, number);

        assertThat(game.getNumber(), equalTo(number));
        assertThat(game.getCurrentPlayer(), equalTo(game.getPlayer1()));
    }

    @Test
    void itShouldFailWhenStartingAStartedGame() {
        Game game = DataProvider.createNewGame();
        UUID starterUserId = game.getPlayer2().getId();
        final int number = 56;

        game.startGame(starterUserId, number);

        // second start
        final int newNumber = 56;
        Assertions.assertThrows(InvalidStartException.class, () -> game.startGame(starterUserId, newNumber));
    }

    @Test
    void itShouldUpdateTheNumberAndCurrentPlayerWhenMoveAStartedGame() {
        Game game = DataProvider.createNewGame();
        final int number = 56;
        game.startGame(game.getPlayer1().getId(), number);

        game.move();

        assertThat(game.getNumber(), equalTo((number + 1) / 3));
        assertThat(game.getCurrentPlayer(), equalTo(game.getPlayer1()));
    }

    @Test
    void itShouldNotAllowMoveWhenGameIsNotStarted() {
        Game game = DataProvider.createNewGame();
        Assertions.assertThrows(InvalidMoveException.class, () -> game.move());
    }

    @Test
    void itShouldEndTheGameWhenPlayingInAutomaticModeAndNumberReach1() {
        Game game = DataProvider.createNewGame();
        final int number = 56;
        game.startGame(game.getPlayer1().getId(), number);

        while (!game.isEnd()) {
            game.move();
        }

        assertThat(game.getNumber(), equalTo(1));
        assertThat(game.getCurrentPlayer(), equalTo(game.getPlayer1()));
    }

}
