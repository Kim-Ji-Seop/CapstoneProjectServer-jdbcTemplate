package com.example.demo.src.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

// 참여가능 매치 총 갯수
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PossibleMatchesRes {
    private int count;
}
