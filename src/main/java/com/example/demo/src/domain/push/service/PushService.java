package com.example.demo.src.domain.push.service;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.domain.history.dao.HistoryDao;
import com.example.demo.src.domain.match.dao.MatchDao;
import com.example.demo.src.domain.match.dto.MatchRoomDetailRes;
import com.example.demo.src.domain.push.dao.PushDao;
import com.example.demo.src.domain.push.dto.JoinAcceptOrNotReq;
import com.example.demo.src.domain.push.dto.JoinAcceptOrNotRes;
import com.example.demo.src.domain.push.dto.MatchJoinPushReq;
import com.example.demo.src.domain.push.dto.MatchJoinPushRes;
import com.example.demo.src.domain.user.dao.UserDao;
import com.example.demo.src.domain.user.dto.UserNameNnickName;
import com.example.demo.utils.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Service
public class PushService {

    private final PushDao pushDao;
    private final UserDao userDao;
    private final MatchDao matchDao;
    private final HistoryDao historyDao;

    private final JwtService jwtService;
    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = { MESSAGING_SCOPE };

    @Autowired
    public PushService(PushDao pushDao, UserDao userDao, MatchDao matchDao, HistoryDao historyDao, JwtService jwtService) {
        this.pushDao = pushDao;
        this.userDao = userDao;
        this.matchDao = matchDao;
        this.historyDao = historyDao;
        this.jwtService = jwtService;
    }

    private String FCM_API_URL = "https://fcm.googleapis.com/v1/projects/capstone-push-51e21/messages:send";

    @Transactional
    public MatchJoinPushRes joinPush(int userIdx, MatchJoinPushReq matchJoinPushReq) throws BaseException {
        try {
            // 1. 매칭 참가 신청 유저 정보 (이름, 닉네임)
            // 2. 신청 대상 매칭 생성자 유저 정보 (이름, 닉네임, FCM 토큰)
            // 3. 매칭방 정보
            int targetUserIdx = matchJoinPushReq.getMatchOwnerUserIdx();
            int matchIdx = matchJoinPushReq.getMatchIdx();
            UserNameNnickName joinUser = userDao.userInfo(userIdx);
            String targetFcmToken = userDao.getTargetFCMtoken(targetUserIdx);
            MatchRoomDetailRes roomDetailRes = matchDao.matchroomDetail(matchIdx);

            // ** 매칭방에 방장이 수락하여 매칭 참여인원이 다 차게 되면 참가신청 불가능
            int currentJoinedUser = matchDao.MatchRoomJoinedUserCount(matchIdx);
            if (currentJoinedUser >= roomDetailRes.getCount()){
                return new MatchJoinPushRes(0);
            }

            // 4. 푸쉬알림 전송 메세지 생성 - 참여 요청
            String push_title = roomDetailRes.getTitle() + " 경기 ("+")";
            String push_content = "<" + roomDetailRes.getDate() + ">\n" + joinUser.getName() + "(" +  joinUser.getNickname() +")" + "님이 참여 요청을 보냈습니다.";

            // 5. 푸쉬 알림 전송
            sendFcmPush(targetFcmToken, push_title, push_content);

            // 6. 푸쉬 알림 메세지 dao 동작 수행
            int pushIdx = pushDao.joinPush(targetUserIdx, userIdx, matchIdx, push_title, push_content);
            return new MatchJoinPushRes(pushIdx);
        }catch (Exception exception){
            //System.out.println(exception);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }


    private static String getAccessToken() throws IOException {
        ClassPathResource resource = new ClassPathResource("keystore/service-account.json");
        InputStream instream = resource.getInputStream();

        GoogleCredential googleCredential = GoogleCredential
                .fromStream(instream) //new FileInputStream(resource.getFile())
                .createScoped(Arrays.asList(SCOPES));
        googleCredential.refreshToken();
        return googleCredential.getAccessToken();
    }

    public JoinAcceptOrNotRes ownerAccepted(int userIdx, JoinAcceptOrNotReq joinAcceptOrNotReq) throws BaseException{
        try{
            // 0) 매칭 참가 요청에 대한 수락:A /거절:D  처리
            String newStatus = joinAcceptOrNotReq.isAccept() ? "A" : "D";
            System.out.println(newStatus);

            // 1. 매칭 참가 신청 유저 정보 (이름, 닉네임)
            // 2. 신청 대상 매칭 생성자 유저 정보 (이름, 닉네임, FCM 토큰)
            // 3. 매칭방 정보
            int targetUserIdx = joinAcceptOrNotReq.getJoin_userIdx();
            int matchIdx = joinAcceptOrNotReq.getMatchIdx();
            UserNameNnickName ownerUser = userDao.userInfo(userIdx); // 매칭 방장의 유저 Info
            String targetFcmToken = userDao.getTargetFCMtoken(targetUserIdx); // 매칭 신청자에게 수락/거절 메세지를 보낼 FCM 토큰
            MatchRoomDetailRes roomDetailRes = matchDao.matchroomDetail(matchIdx); // 매칭방 정보

            // 4. 푸쉬알림 전송 메세지 생성 - 방장이 참여자를 수락/거절
            String push_title = roomDetailRes.getTitle() + " 경기 ("+")";
            String push_content = "<" + roomDetailRes.getDate() + ">\n" + ownerUser.getName() + "(" +  ownerUser.getNickname() +")";

            // ** 매칭방에 방장이 수락하려는데 매칭 참여인원이 다 찼으면 수락 불가능
            int currentJoinedUser = matchDao.MatchRoomJoinedUserCount(matchIdx);
            if (currentJoinedUser >= roomDetailRes.getCount()){
                push_content = "<" + roomDetailRes.getDate() + ">\n"+ "남은 자리가 없습니다.";
                newStatus = "D";
            }

            int pushIdx = pushDao.ownerAccepted(userIdx, joinAcceptOrNotReq, newStatus);

            // 4-1) 수락 시
            if (newStatus == "A"){
                int teamIdx = 0;
                /* team 설정 로직 추가할것*/

                historyDao.createMatchRoomNewPlayer(
                        joinAcceptOrNotReq.getJoin_userIdx(),
                        joinAcceptOrNotReq.getMatchIdx(),
                        teamIdx);
                push_content += "님이 참여 요청을 수락하였습니다.";
            }
            // 4-2) 거절 시
            else if (newStatus == "D"){
                push_content += "님이 참여 요청을 거절하였습니다.";
            }

            sendFcmPush(targetFcmToken, push_title, push_content);

            return new JoinAcceptOrNotRes(pushIdx);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }

    private void sendFcmPush(String targetFcmToken, String push_title, String push_content) {
        JSONObject jsonValue = new JSONObject();
        jsonValue.put("title", push_title);
        jsonValue.put("content", push_content);

        JSONObject jsonData = new JSONObject();
        jsonData.put("token", targetFcmToken);
        jsonData.put("data", jsonValue);

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("message", jsonData);

        // 2. create token & send push
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + getAccessToken())
                    .addHeader("Content-Type", "application/json; UTF-8")
                    .url(FCM_API_URL)
                    .post(RequestBody.create(jsonMessage.toString(), MediaType.parse("application/json")))
                    .build();
            Response response = okHttpClient.newCall(request).execute();

            System.out.println("### response str : " + response.toString());
            System.out.println("### response result : " + response.isSuccessful());
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
