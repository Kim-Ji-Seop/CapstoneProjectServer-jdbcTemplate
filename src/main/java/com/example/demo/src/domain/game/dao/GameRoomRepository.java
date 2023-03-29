package com.example.demo.src.domain.game.dao;

import com.example.demo.src.domain.game.dto.GameRoomDTO;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

@Repository
public class GameRoomRepository {

    private Map<String, GameRoomDTO> chatRoomDTOMap;

    @PostConstruct
    private void init(){
        chatRoomDTOMap = new LinkedHashMap<>();
    }

    public List<GameRoomDTO> findAllRooms(){
        //채팅방 생성 순서 최근 순으로 반환
        List<GameRoomDTO> result = new ArrayList<>(chatRoomDTOMap.values());
        Collections.reverse(result);

        return result;
    }

    public GameRoomDTO findRoomById(String id){
        return chatRoomDTOMap.get(id);
    }

    public GameRoomDTO matchActivated(String code){
        GameRoomDTO room;
        // 진행 게임 테이블 생성하여 DB에 저장할것 (id, matchIdx, matchCode)
        if (!chatRoomDTOMap.containsKey(code)){
            room = GameRoomDTO.activate(code);
            chatRoomDTOMap.put(code, room);
        }

        return chatRoomDTOMap.get(code);
    }
}
