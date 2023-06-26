package com.capston.bowler.src.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMatchPlanDetailRes {
    private int teamIdx;
    private int userIdx;
    private String nickName;
    private String profile_imgurl;
    private int highScore;
    private int avgScore;
    private int gameCount;
    private int winCount;
    private int loseCount;
    private String homeOrAway;
}
