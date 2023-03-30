package com.example.demo.src.domain.game.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GameRoomDTO {

    private String roomId;
    private String code;
    // private Set<WebSocketSession> sessions = new HashSet<>();
    // WebSocketSession은 Spring에서 Websocket Connection이 맺어진 세션

    public static GameRoomDTO activate(String code){
        GameRoomDTO room = new GameRoomDTO();

        room.roomId = UUID.randomUUID().toString();
        room.code = code;
        return room;
    }
}
