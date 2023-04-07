package com.example.demo.src.domain.match.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserHistoryInfo {
    private int userIdx;
    private int matchIdx;
    private int teamIdx;
    private String settle_type;
    private int total_score;
}
