package com.capston.bowler.src.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private int userIdx;
    private String uid;
    private String password;
    private String name;
    private String nickname;
}
