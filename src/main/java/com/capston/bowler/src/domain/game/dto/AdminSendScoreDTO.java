package com.capston.bowler.src.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminSendScoreDTO {
    private int playerNum;
    private String matchIdx;
    private String writer;
    private int score;
}
