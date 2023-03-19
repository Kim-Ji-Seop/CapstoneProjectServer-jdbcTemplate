package com.example.demo.src.domain.score.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewMatchOpenRes {
    private String matchCode;
    private int matchIdx;
}
