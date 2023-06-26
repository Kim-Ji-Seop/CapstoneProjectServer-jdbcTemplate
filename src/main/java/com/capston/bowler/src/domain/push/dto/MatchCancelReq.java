package com.capston.bowler.src.domain.push.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchCancelReq {
    private int matchIdx;
    private List<MatchCancelUser> matchCancelUserList;
}
