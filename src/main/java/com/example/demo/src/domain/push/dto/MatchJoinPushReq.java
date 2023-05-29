package com.example.demo.src.domain.push.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchJoinPushReq {
    int matchOwnerUserIdx; // 매칭방 생성 유저 Id
    int matchIdx; // 매칭방 Id
}
