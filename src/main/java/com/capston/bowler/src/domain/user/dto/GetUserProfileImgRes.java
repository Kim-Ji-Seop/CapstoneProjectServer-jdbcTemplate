package com.capston.bowler.src.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserProfileImgRes {
    private int userIdx;
    private String userProfileImgUrl;
}
