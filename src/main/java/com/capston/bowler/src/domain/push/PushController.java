package com.capston.bowler.src.domain.push;

import com.capston.bowler.config.BaseException;
import com.capston.bowler.config.BaseResponse;
import com.capston.bowler.src.domain.push.dto.*;
import com.capston.bowler.src.domain.push.service.PushService;
import com.capston.bowler.utils.JwtService;
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
     * [POST] 서비스단에서 포스트 /push/
     * @return
     */
    @RequestMapping("")
    public BaseResponse<MatchJoinPushRes> joinPush(@RequestBody MatchJoinPushReq matchJoinPushReq) throws BaseException {
        int userIdx = jwtService.getUserIdx();
        try{
            MatchJoinPushRes matchJoinPushRes = pushService.joinPush(userIdx, matchJoinPushReq);
            return new BaseResponse<>(matchJoinPushRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }

    }

    /**
     * 매칭방 참여 신청 수락 API
     * [POST] 서비스단에서 포스트 /push/
     * @return
     */
    @RequestMapping("/permission")
    public BaseResponse<JoinAcceptOrNotRes> ownerAccepted(@RequestBody JoinAcceptOrNotReq joinAcceptOrNotReq) throws BaseException{
        int userIdx = jwtService.getUserIdx();
        try{
            JoinAcceptOrNotRes joinAcceptOrNotRes = pushService.ownerAccepted(userIdx, joinAcceptOrNotReq);
            return new BaseResponse<>(joinAcceptOrNotRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 매칭방 참여 신청 거절 API
     * [POST] 서비스단에서 포스트 /push/
     * @return
     */
    @RequestMapping("/cancel")
    public BaseResponse<Integer> matchCancel(@RequestBody MatchCancelReq matchCancelReq) throws BaseException{
        int userIdx = jwtService.getUserIdx();
        try{
            return new BaseResponse<>(pushService.matchCancel(userIdx,matchCancelReq));
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


}