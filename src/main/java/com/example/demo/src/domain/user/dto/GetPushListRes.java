package com.example.demo.src.domain.user.dto;

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
    private int owner_userIdx;
    private int join_userIdx;
    private int matchIdx;
    private String push_title;
    private String push_content;
    private String created;
    private String updated;
    private String onlydate;
    private String status;
}
