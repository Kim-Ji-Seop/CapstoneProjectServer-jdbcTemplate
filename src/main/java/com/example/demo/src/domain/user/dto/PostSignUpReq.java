package com.example.demo.src.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostSignUpReq {
    private String uid; // 아이디
    private String password; // 비밀번호
    private String name; // 이름
    private String nickName; // 닉네임
}
