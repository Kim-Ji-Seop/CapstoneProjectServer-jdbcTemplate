package com.capston.bowler.src.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCheckSocketActiveRes {
    private String status;
    private List<HistoryInfo> historyInfo;
}
