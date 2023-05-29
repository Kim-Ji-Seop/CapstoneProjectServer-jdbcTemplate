package com.example.demo.src.domain.game.dto;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import javax.annotation.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameEndReq {

    private int historyIdx;

    @SerializedName("frameScoresPerPitch")
    private int[][] frameScoresPerPitch;

    @SerializedName("frameScores")
    private int [] frameScores;

    @Nullable
    private String settle_type;


}
