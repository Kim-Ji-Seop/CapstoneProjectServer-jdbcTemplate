package com.example.demo.src.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ByNetworkRes {
    private String date; // 경기 날짜
    private int average; // 점수대
    private String place; // 경기 장소
    private int numbers; // 인원 수

    // 온라인 매칭방 리스트
    public ByNetworkRes(String date, int average) {
        this.date = date;
        this.average = average;
    }
}
