package com.example.demo.src.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class PostCreateMatchRoomReq {
    private String title; // 제목
    private String content; // 내용
    private String date; // 경기 시간
    private int number; // 인원 수
    private String location; // 지역
    private String place; // 장소
    private int average; // 팀 avg
    private String networkType; // 네트워크 타입
    private int cost; // 비용
}
