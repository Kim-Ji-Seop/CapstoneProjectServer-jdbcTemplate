package com.example.demo.src.domain.game.websock;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.domain.game.dto.*;
import com.example.demo.src.domain.game.service.GameRoomService;
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

    /*@ResponseBody
    @PostMapping("/over")
    public BaseResponse<GameEndRes> matchFinished(@RequestBody List<GameEndReq> gameEndReq){
        try{
            //System.out.println(gameEndReq);
            GameEndRes gameEndRes = gameRoomService.matchFinished(gameEndReq);

            return null;
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }*/

    /*@ResponseBody
    @GetMapping("/a")
    public BaseResponse<ArrayRes> checkArray(){
        int [] a = new int[]{1,2,3,4};
        ArrayRes ar = new ArrayRes();
        ar.setArrays(a);
        return new BaseResponse<>(ar);
    }
*/

}
