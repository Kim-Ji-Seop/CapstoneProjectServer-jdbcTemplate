package com.example.demo.src.domain.push;

import com.example.demo.config.BaseException;
import com.example.demo.src.domain.push.dto.MatchJoinPushReq;
import com.example.demo.src.domain.push.dto.MatchJoinPushRes;
import com.example.demo.src.domain.push.service.PushService;
import com.example.demo.utils.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@RestController
@RequestMapping("/app/push")
@RequiredArgsConstructor
public class PushController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PushService pushService;

    private final JwtService jwtService;


    /**
     * 매칭방 참여 신청 API
     * [POST] 서비스단에서 포스트 /push/send
     * @return
     */
    @RequestMapping("/send")
    public void send(@RequestBody MatchJoinPushReq matchJoinPushReq) throws BaseException {
       //int userIdx = jwtService.getUserIdx();
       int userIdx = 14; // 테스트용 jwt ID 가정
       pushService.send(userIdx, matchJoinPushReq);

    }


}