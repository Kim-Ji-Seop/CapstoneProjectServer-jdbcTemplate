package com.example.demo.src.domain.game.websock;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.domain.game.dto.ChatRoomDTO;
import com.example.demo.src.domain.game.dto.NewMatchOpenRes;
import com.example.demo.src.domain.game.service.MatchCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping( "/app/game")
public class MatchCodeController {
    @Autowired
    private MatchCodeService matchCodeService;


    public MatchCodeController(MatchCodeService matchCodeService) {
        this.matchCodeService = matchCodeService;
    }

    @PostMapping ("/open")
    public BaseResponse<ChatRoomDTO> openNewMatch(@RequestParam String id){
        try{
            return new BaseResponse<>(matchCodeService.openNewMatch(id));
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
