package com.example.demo.src.domain.game.websock;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping( "/app/game")
public class MatchCodeController {

    /*@GetMapping("/open/{matchCode}")
    public BaseResponse<NewMatchOpenRes> openNewMatch();*/
}
