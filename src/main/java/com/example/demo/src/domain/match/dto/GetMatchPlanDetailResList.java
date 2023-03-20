package com.example.demo.src.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMatchPlanDetailResList {
    private int matchIdx;
    private String gameTime;
    private String matchCode;
    private List<GetMatchPlanDetailRes> getMatchPlanDetailRes;
}
