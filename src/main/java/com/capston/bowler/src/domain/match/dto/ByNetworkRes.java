package com.capston.bowler.src.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ByNetworkRes {
    private String date; // 경기 날짜
    private int average; // 점수대
    private String place; // 경기 장소
    private int numbers; // 인원 수
    private int matchIdx; // 매칭방Idx

    // 온라인 매칭방 리스트
    public ByNetworkRes(String date, int average, int matchIdx) {
        this.date = date;
        this.average = average;
        this.matchIdx = matchIdx;
    }
}
