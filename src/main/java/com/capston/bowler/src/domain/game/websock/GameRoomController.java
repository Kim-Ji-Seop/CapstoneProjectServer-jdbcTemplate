package com.capston.bowler.src.domain.game.websock;


import com.capston.bowler.config.BaseException;
import com.capston.bowler.config.BaseResponse;
import com.capston.bowler.src.domain.game.dto.*;
import com.capston.bowler.src.domain.game.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/app/game")
public class GameRoomController {
    @Autowired
    private GameRoomService gameRoomService;

    public GameRoomController(GameRoomService gameRoomService) {
        this.gameRoomService = gameRoomService;
    }


    @ResponseBody
    @PostMapping("/match-code") // admin
    public BaseResponse<PostMatchCodeRes> getRoomIdx(@RequestBody PostMatchCodeReq postMatchCodeReq){
        try{
            PostMatchCodeRes postMatchCodeRes = gameRoomService.getRoomIdx(postMatchCodeReq);
            return new BaseResponse<>(postMatchCodeRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/join") // client
    public BaseResponse<PostCheckSocketActiveRes> getRoomStatus(@RequestBody PostCheckSocketActiveReq postCheckSocketActiveReq){
        try{
            PostCheckSocketActiveRes postMatchCodeRes = gameRoomService.getRoomStatus(postCheckSocketActiveReq);
            return new BaseResponse<>(postMatchCodeRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/over")
    public BaseResponse<GameEndRes> matchFinished(@RequestBody List<GameEndReq> gameEndReq){
        try{
            //System.out.println(gameEndReq);
            gameRoomService.matchFinished(gameEndReq);
            return new BaseResponse<>(new GameEndRes(-1));
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
