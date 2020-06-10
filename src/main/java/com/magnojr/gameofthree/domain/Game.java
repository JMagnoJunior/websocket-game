package com.magnojr.gameofthree.domain;


import com.magnojr.gameofthree.exception.InvalidMoveException;
import com.magnojr.gameofthree.exception.InvalidStartException;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Game {

    private UUID id;
    private Player player1;
    private Player player2;
    private int number;
    private Player currentPlayer;

    public Game(UUID id, Player player1, Player player2) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
    }

    public void startGame(UUID starter, final int number) {

        if (isStarted()) {
            throw new InvalidStartException("Game already started");
        }

        this.number = number;
        this.currentPlayer = starter.equals(player1.getId()) ? player2 : player1;
    }

    public int move(int addedValue) {
        if (!isStarted()) {
            throw new InvalidMoveException("The game is not started");
        }

        if (!isValidAdditionalNumber(addedValue)) {
            throw new InvalidMoveException("the added number plus current number can not be divided by 3. Please, type another value to add.");
        }

        this.number = getNextNumber(addedValue);

        if (isEnd()) {
            return this.number;
        } else {
            this.currentPlayer = getNextPlayer();
            return this.number;

        }

    }

    public int move() {
        if (!isStarted()) {
            throw new InvalidMoveException("The game is not started");
        }

        this.number = getNextNumber();

        if (isEnd()) {
            return this.number;
        } else {
            this.currentPlayer = getNextPlayer();
            return this.number;

        }

    }

    public boolean isStarted() {
        return this.number != 0;
    }

    public boolean isEnd() {
        if (number == 1) {
            return true;
        }

        return false;
    }

    private int getNextNumber() {
        return getNextNumber(getAdditionalNumberValidNumberForMove());
    }

    private int getNextNumber(int addedValue) {
        return (this.number + addedValue) / 3;
    }

    private Player getNextPlayer() {
        return player1 == this.currentPlayer ? player2 : player1;
    }

    private int getAdditionalNumberValidNumberForMove() {
        for (int i = -1; i <= 1; i++) {
            if (isValidAdditionalNumber(i)) {
                return i;
            }
        }
        throw new RuntimeException("There are no valid numbers for the next move");
    }

    private boolean isValidAdditionalNumber(int value) {
        List<Integer> validValueList = List.of(-1, 0, 1);
        if (!validValueList.contains(value)) {
            throw new InvalidMoveException("Value not accepted. Select a possible values: {-1, 0, 1} ");
        }

        if (((this.number + value) % 3) == 0) {
            return true;
        } else {
            return false;
        }
    }

}
