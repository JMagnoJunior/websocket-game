package com.magnojr.gameofthree.services;

import com.magnojr.gameofthree.domain.Game;
import com.magnojr.gameofthree.domain.Player;
import com.magnojr.gameofthree.dto.MoveDTO;
import com.magnojr.gameofthree.dto.StartGameMoveDTO;
import com.magnojr.gameofthree.exception.InvalidMoveException;
import com.magnojr.gameofthree.exception.InvalidStartException;

import java.util.*;

public class GameService {

    private final HashMap<UUID, Game> games;
    private final Queue<Player> waitingList;
    private final UserMessageService userMessageService;

    public GameService(final UserMessageService userMessageService, final Queue<Player> waitingList, final HashMap<UUID, Game> games) {
        this.games = games;
        this.waitingList = waitingList;
        this.userMessageService = userMessageService;
    }

    public void loadingGame(final UUID id, final String userName) {
        final Player newPlayer = new Player(id, userName);

        if (waitingList.isEmpty()) {
            addToWaitingList(newPlayer);
        } else {
            createNewGame(newPlayer);
        }
    }

    public void startGame(final UUID userId, final StartGameMoveDTO startGameMoveDTO) {
        if (!games.containsKey(startGameMoveDTO.getGameId())) {
            userMessageService.sendErrorMessage(userId, "A new player has to join before you start this game");
            return;
        }

        Game game = games.get(startGameMoveDTO.getGameId());
        try {
            game.startGame(userId, startGameMoveDTO.getNumber());
            userMessageService.sendStartingGameMessage(userId, game);
        } catch (InvalidStartException e) {
            userMessageService.sendErrorMessage(userId, e.getMessage());
        }
    }

    public void nextMove(final UUID userId, final MoveDTO move) {
        Game game = games.get(move.getGameId());
        UUID previousPlayer = game.getCurrentPlayer().getId();
        if (!game.getCurrentPlayer().getId().equals(userId)) {
            userMessageService.sendWaitForYourTurnMessage(userId);
            return;
        }
        try {
            if (move.getAddedValue() == null) {
                game.move();
            } else {
                game.move(move.getAddedValue());
            }
            userMessageService.sendMoveMessage(previousPlayer, game);
        } catch (InvalidMoveException e) {
            userMessageService.sendErrorMessage(userId, e.getMessage());
        }
    }

    private void createNewGame(final Player player2) {
        final Player player1 = waitingList.poll();
        final UUID newGameId = UUID.randomUUID();
        final Game game = new Game(newGameId, player1, player2);
        userMessageService.sendCreateNewGameMessage(game);
        games.put(newGameId, game);
    }

    private void addToWaitingList(Player newPlayer) {
        waitingList.add(newPlayer);
        userMessageService.sendWaitForOpponentMessage(newPlayer.getId());
    }

}
