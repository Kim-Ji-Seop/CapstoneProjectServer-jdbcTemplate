package com.example.demo.src.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileInfo {
    private String name;
    private String nickName;
    private int winCount;
    private int loseCount;
    private int drawCount;
    private int avg;
    private int highScore;
    private double strikeRate;
    private int gameCount;
}
