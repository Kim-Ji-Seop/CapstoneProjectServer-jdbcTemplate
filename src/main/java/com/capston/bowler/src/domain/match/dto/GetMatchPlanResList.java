package com.capston.bowler.src.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMatchPlanResList {
    private int matchIdx;
    private String game_time;
    private String network_type;
    private List<GetMatchPlanRes> getMatchPlanResList;
}
