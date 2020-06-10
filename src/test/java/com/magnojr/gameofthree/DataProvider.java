package com.magnojr.gameofthree;

import com.magnojr.gameofthree.domain.Game;
import com.magnojr.gameofthree.domain.Player;

import java.util.UUID;

public class DataProvider {


    public static Game createNewGame() {
        Player player1 = new Player(UUID.randomUUID(), "P1");
        Player player2 = new Player(UUID.randomUUID(), "P2");
        return new Game(UUID.randomUUID(), player1, player2);

    }
}
