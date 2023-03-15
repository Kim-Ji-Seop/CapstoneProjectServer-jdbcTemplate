package com.example.demo.src.domain.push.service;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.domain.match.dao.MatchDao;
import com.example.demo.src.domain.match.dto.MatchRoomDetailRes;
import com.example.demo.src.domain.push.dao.PushDao;
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

    @Transactional
    public MatchJoinPushRes joinPush(int userIdx, MatchJoinPushReq matchJoinPushReq) throws BaseException {
        // userIdx -> 참가자
        // targetUserIdx -> 방장
        // matchIdx -> 매칭방 번호
        // push_title
        // push_content

        // 1. 매칭 참가 신청 유저 정보 (이름, 닉네임)
        UserNameNnickName joinUser = userDao.userInfo(userIdx);

        // 2. 신청 대상 매칭 생성자 유저 정보 (이름, 닉네임, FCM 토큰)
        int targetUserIdx = matchJoinPushReq.getMatchOwnerUserIdx();
        //UserNameNnickName ownerUser = userDao.userInfo(targetUserIdx);
        String targetFcmToken = userDao.getTargetFCMtoken(targetUserIdx);

        // 3. 매칭방 정보
        int matchIdx = matchJoinPushReq.getMatchIdx();
        MatchRoomDetailRes roomDetailRes = matchDao.matchroomDetail(matchIdx);


        // 4. 푸쉬알림 전송 메세지 생성
        String push_title = roomDetailRes.getTitle() + " 경기";
        String push_content = "<" + roomDetailRes.getDate() + ">\n" + joinUser.getName() + "(" +  joinUser.getNickname() +")" + "님이 참여 요청을 보냈습니다.";

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        // dao 동작 수행
        try {
            int pushIdx = pushDao.sendPush(targetUserIdx, userIdx, matchIdx, push_title, push_content);
            return new MatchJoinPushRes(pushIdx);
        }catch (Exception exception){
            System.out.println(exception);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
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
