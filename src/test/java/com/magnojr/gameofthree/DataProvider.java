package com.magnojr.gameofthree;

import com.magnojr.gameofthree.domain.Game;
import com.magnojr.gameofthree.domain.Player;

import java.util.Random;
import java.util.UUID;

public class DataProvider {


    public static Game createNewGame() {
        Player player1 = new Player(UUID.randomUUID(), "P1");
        Player player2 = new Player(UUID.randomUUID(), "P2");
        return new Game(UUID.randomUUID(), player1, player2);

    }

    public static int getRandomValue(final int rangeMin, final int rangeMax) {
        Random r = new Random();
        return r.nextInt((rangeMax - rangeMin) + 1) + rangeMin;
    }

}
