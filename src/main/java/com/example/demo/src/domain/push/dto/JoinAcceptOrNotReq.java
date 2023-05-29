package com.example.demo.src.domain.push.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinAcceptOrNotReq {
    private int pushIdx;
    private int owner_userIdx;
    private int join_userIdx;
    private int matchIdx;
    private boolean accept;
}
