package com.example.demo.src.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMatchPlanRes {
    private String game_time;
    private String network_type;
    private String nickname;
    private int count;
    private int historyIdx;
    private int userIdx;
    private int matchIdx;
    private int teamIdx;
    private String homeOrAway;
    private String place;
}
