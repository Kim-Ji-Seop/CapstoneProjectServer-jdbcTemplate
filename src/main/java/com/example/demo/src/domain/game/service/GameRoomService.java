package com.example.demo.src.domain.game.service;

import com.example.demo.config.BaseException;
import com.example.demo.src.domain.game.dao.GameRoomDao;
import com.example.demo.src.domain.game.dao.GameRoomRepository;
import com.example.demo.src.domain.game.dto.GameRoomDTO;
import com.example.demo.src.domain.game.dto.PostMatchCodeReq;
import com.example.demo.src.domain.game.dto.PostMatchCodeRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class GameRoomService {
    private final GameRoomRepository gameRoomRepository;
    private final GameRoomDao gameRoomDao;
    @Autowired
    public GameRoomService(GameRoomRepository gameRoomRepository,GameRoomDao gameRoomDao) {
        this.gameRoomRepository = gameRoomRepository;
        this.gameRoomDao = gameRoomDao;
    }


    public GameRoomDTO matchActivated(String code) throws BaseException {
        try{
            GameRoomDTO newMatchOpenRes = gameRoomRepository.matchActivated(code);
            System.out.println("openNewMatch: " + newMatchOpenRes.getRoomId());
            return newMatchOpenRes;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional
    public PostMatchCodeRes getRoomIdx(PostMatchCodeReq postMatchCodeReq) throws BaseException{
        try{
            // RoomIdx 변경
            PostMatchCodeRes postMatchCodeRes = gameRoomDao.getRoomIdx(postMatchCodeReq);
            // 매칭방 상태변경 -> 소켓 활성화(WA)
            gameRoomDao.updateMatchRoomStatus(postMatchCodeRes);

            return postMatchCodeRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
