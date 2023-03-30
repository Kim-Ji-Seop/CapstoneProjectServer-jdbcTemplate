package com.example.demo.src.domain.game.websock;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.domain.game.dto.GameRoomDTO;
import com.example.demo.src.domain.game.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping ("/open")
    public BaseResponse<GameRoomDTO> matchActivated(@RequestParam String id){
        try{
            System.out.println("Match Room: " + id + " activated");
            return new BaseResponse<>(gameRoomService.matchActivated(id));
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


}
