package com.capston.bowler.src.domain.match.dto;

import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateMatchRoomReq {
    private String title; // 제목
    private String content; // 내용
    private String date; // 경기 시간
    private int count; // 인원 수
    @Nullable private String location; // 지역
    @Nullable private String place; // 장소
    @Nullable private String localName; // db 지역 명
    @Nullable private String cityName; // db 도시 명
    private int average; // 팀 avg
    private String networkType; // 네트워크 타입
    private int cost; // 비용

}
