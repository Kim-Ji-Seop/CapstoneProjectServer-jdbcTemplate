package com.example.demo.src.domain.score.websock;


import com.example.demo.config.BaseResponse;
import com.example.demo.src.domain.score.dto.NewMatchOpenRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping( "/app/score")
public class MatchCodeController {

    /*@GetMapping("/open/{matchCode}")
    public BaseResponse<NewMatchOpenRes> openNewMatch();*/
}
