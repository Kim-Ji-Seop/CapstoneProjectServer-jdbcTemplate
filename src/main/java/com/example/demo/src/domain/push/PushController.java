package com.example.demo.src.domain.push;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.domain.push.dto.MatchJoinPushReq;
import com.example.demo.src.domain.push.dto.MatchJoinPushRes;
import com.example.demo.src.domain.push.service.PushService;
import com.example.demo.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/pushes")
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
    @RequestMapping("")
    public BaseResponse<MatchJoinPushRes> send(@RequestBody MatchJoinPushReq matchJoinPushReq) throws BaseException {
        int userIdx = jwtService.getUserIdx();
        //int userIdx = 17; // 테스트용 jwt ID 가정
        try{
            MatchJoinPushRes matchJoinPushRes = pushService.joinPush(userIdx, matchJoinPushReq);
            return new BaseResponse<>(matchJoinPushRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }

    }

    /*@RequestMapping("/permission")
    public BaseResponse<>*/


}