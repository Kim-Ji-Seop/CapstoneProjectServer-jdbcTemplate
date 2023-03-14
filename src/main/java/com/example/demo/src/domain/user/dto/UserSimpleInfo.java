package com.example.demo.src.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSimpleInfo {
    private String name; // 이름
    private String nickname; // 닉네임
    private int average; // 점수 평균
    // 최근 10 경기 승, 패
    private int win_count;
    private int lose_count;
    private int win_late;

}
