package com.capston.bowler.src.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetPushListResByDateArr {
    private String date;
    private List<GetPushListRes> alarmDetail;
}
