package com.magnojr.gameofthree.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartGameMoveDTO {

    private UUID gameId;
    private int number;
}
