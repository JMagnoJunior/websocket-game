package com.magnojr.gameofthree.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoveDTO {

    private UUID gameId;
    private Integer addedValue;

    public MoveDTO(UUID gameId) {
        this.gameId = gameId;
    }

}
