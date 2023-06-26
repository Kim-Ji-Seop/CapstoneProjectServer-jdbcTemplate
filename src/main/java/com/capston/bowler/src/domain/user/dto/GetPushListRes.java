package com.capston.bowler.src.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetPushListRes {
    private int pushIdx;
    private String profileImg_url;
    private String opponentNick;
    private int owner_userIdx;
    private int join_userIdx;
    private int matchIdx;
    private String game_time;
    private String network_type;
    private String onlydate;
    private String status;
}
