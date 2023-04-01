package com.example.demo.src.domain.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSendScoreDTO {
    private String matchIdx;
    private String writer;
    private int frame;
    private int score;
}
