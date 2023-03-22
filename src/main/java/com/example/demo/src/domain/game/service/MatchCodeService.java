package com.example.demo.src.domain.game.service;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.domain.game.dao.GameRoomRepository;
import com.example.demo.src.domain.game.dto.ChatRoomDTO;
import com.example.demo.src.domain.game.dto.NewMatchOpenRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class MatchCodeService {
    private final GameRoomRepository gameRoomRepository;

    @Autowired
    public MatchCodeService(GameRoomRepository gameRoomRepository) {
        this.gameRoomRepository = gameRoomRepository;
    }


    public ChatRoomDTO openNewMatch(String id) throws BaseException {
        try{
            ChatRoomDTO newMatchOpenRes = gameRoomRepository.createChatRoomDTO(id);
            System.out.println("openNewMatch: " + newMatchOpenRes.getRoomId());
            return newMatchOpenRes;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
