package com.example.demo.src.domain.match.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HAmatchRecordsRes { // Home & Away
    int matchIdx;
    private List<MatchRecordsRes> matchRecordsResList;
}
