package com.example.demo.src.domain.game.dto;

import com.google.firebase.database.annotations.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {

    private String matchIdx;
    private String writer;
    @Nullable private String message;
}