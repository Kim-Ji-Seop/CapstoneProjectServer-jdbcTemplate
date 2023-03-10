package com.example.demo.src.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchRoomDetailRes {
    private String date; // 경기날짜
    private String nickname; // 게시자 닉네임
    private String title;
    private String content;
    private int count; // 인원 수
    private int targetScore;
    private int cost;
    private String location;
    private String place;
}
