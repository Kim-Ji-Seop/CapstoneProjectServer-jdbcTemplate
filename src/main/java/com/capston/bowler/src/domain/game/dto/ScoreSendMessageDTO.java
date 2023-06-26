package com.capston.bowler.src.domain.game.dto;

import com.google.firebase.database.annotations.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoreSendMessageDTO {

    private String matchIdx;
    private String writer;
    @Nullable private String message;
}