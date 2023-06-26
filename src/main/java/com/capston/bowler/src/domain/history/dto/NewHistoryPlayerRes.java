package com.capston.bowler.src.domain.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewHistoryPlayerRes {
    private int historyIdx;
    private int matchRoomIdx;
    private int userIdx;
    private int teamIdx;
}
