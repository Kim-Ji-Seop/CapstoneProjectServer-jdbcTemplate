package com.example.demo.src.domain.push.service;

import com.example.demo.src.domain.match.dao.MatchDao;
import com.example.demo.src.domain.match.dto.MatchRoomDetailRes;
import com.example.demo.src.domain.push.dao.PushDao;
import com.example.demo.src.domain.push.dto.MatchJoinPushReq;
import com.example.demo.src.domain.push.dto.MatchJoinPushRes;
import com.example.demo.src.domain.user.dao.UserDao;
import com.example.demo.src.domain.user.dto.UserSimpleInfo;
import com.example.demo.utils.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@Service
public class PushService {

    private final PushDao pushDao;
    private final UserDao userDao;
    private final MatchDao matchDao;

    private final JwtService jwtService;
    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = { MESSAGING_SCOPE };

    @Autowired
    public PushService(PushDao pushDao, UserDao userDao, MatchDao matchDao, JwtService jwtService) {
        this.pushDao = pushDao;
        this.userDao = userDao;
        this.matchDao = matchDao;
        this.jwtService = jwtService;
    }

    private String FCM_API_URL = "https://fcm.googleapis.com/v1/projects/capstone-push-51e21/messages:send";

    public void send(int userIdx, MatchJoinPushReq matchJoinPushReq){
        // 1. 매칭 참가 신청 유저 정보 (이름, 닉네임)
        UserSimpleInfo joinUser = userDao.userInfo(userIdx);

        // 2. 신청 대상 매칭 생성자 유저 정보 (이름, 닉네임, FCM 토큰)
        int targetUserIdx = matchJoinPushReq.getMatchOwnerUserIdx();
        //UserSimpleInfo ownerUser = userDao.userInfo(targetUserIdx);
        String targetFcmToken = userDao.getTargetFCMtoken(targetUserIdx);

        // 3. 매칭방 정보
        int matchIdx = matchJoinPushReq.getMatchIdx();
        MatchRoomDetailRes roomDetailRes = matchDao.matchroomDetail(matchIdx);

        // 4. 푸쉬알림 전송 메세지 생성
        JSONObject jsonValue = new JSONObject();
        jsonValue.put("title", roomDetailRes.getTitle() + " 경기");
        jsonValue.put("content", joinUser.getName() + "(" +  joinUser.getNickname() +")" + "님이 참여 요청을 보냈습니다.");

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getAccessToken() throws IOException {
        ClassPathResource resource = new ClassPathResource("keystore/service-account.json");
        GoogleCredential googleCredential = GoogleCredential
                .fromStream(new FileInputStream(resource.getFile()))
                .createScoped(Arrays.asList(SCOPES));
        googleCredential.refreshToken();
        return googleCredential.getAccessToken();
    }
}
