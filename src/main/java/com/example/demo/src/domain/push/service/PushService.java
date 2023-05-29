package com.example.demo.src.domain.push.service;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.domain.history.dao.HistoryDao;
import com.example.demo.src.domain.match.dao.MatchDao;
import com.example.demo.src.domain.match.dto.MatchRoomDetailRes;
import com.example.demo.src.domain.push.dao.PushDao;
import com.example.demo.src.domain.push.dto.*;
import com.example.demo.src.domain.user.dao.UserDao;
import com.example.demo.src.domain.user.dto.UserNameNickName;
import com.example.demo.utils.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
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
    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging"; // googleAPI 인가 url
    private static final String[] SCOPES = { MESSAGING_SCOPE };

    @Autowired
    public PushService(PushDao pushDao, UserDao userDao, MatchDao matchDao, HistoryDao historyDao, JwtService jwtService) {
        this.pushDao = pushDao;
        this.userDao = userDao;
        this.matchDao = matchDao;
        this.historyDao = historyDao;
        this.jwtService = jwtService;
    }

    // 푸쉬 알림 메세지를 보낼 파이어베이스 URL
    private String FCM_API_URL = "https://fcm.googleapis.com/v1/projects/capstone-push-51e21/messages:send";

    // 매칭방 참가 신청 알림
    @Transactional
    public MatchJoinPushRes joinPush(int userIdx, MatchJoinPushReq matchJoinPushReq) throws BaseException {
        try {
            // 1. 매칭 참가 신청 유저 정보 (이름, 닉네임)
            // 2. 신청 대상 매칭 생성자 유저 정보 (이름, 닉네임, FCM 토큰)
            // 3. 매칭방 정보
            int targetUserIdx = matchJoinPushReq.getMatchOwnerUserIdx();
            int matchIdx = matchJoinPushReq.getMatchIdx();
            UserNameNickName joinUser = userDao.userInfo(userIdx);
            String targetFcmToken = userDao.getTargetFCMtoken(targetUserIdx);
            MatchRoomDetailRes roomDetailRes = matchDao.matchroomDetail(matchIdx);

            // ** 매칭방에 방장이 수락하여 매칭 참여인원이 다 차게 되면 참가신청 불가능
            int currentJoinedUser = matchDao.matchRoomJoinedUserCount(matchIdx);
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

    // access token을 파이어베이스에서 제공받은 키 파일에서 가져옴
    private static String getAccessToken() throws IOException {
        ClassPathResource resource = new ClassPathResource("keystore/service-account.json");
        InputStream instream = resource.getInputStream();

        GoogleCredential googleCredential = GoogleCredential
                .fromStream(instream) //new FileInputStream(resource.getFile())
                .createScoped(Arrays.asList(SCOPES));
        googleCredential.refreshToken();
        return googleCredential.getAccessToken();
    }


    // 매칭방 참가 수락 알림
    @Transactional
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
            UserNameNickName ownerUser = userDao.userInfo(userIdx); // 매칭 방장의 유저 Info
            String targetFcmToken = userDao.getTargetFCMtoken(targetUserIdx); // 매칭 신청자에게 수락/거절 메세지를 보낼 FCM 토큰
            MatchRoomDetailRes roomDetailRes = matchDao.matchroomDetail(matchIdx); // 매칭방 정보

            // 4. 푸쉬알림 전송 메세지 생성 - 방장이 참여자를 수락/거절
            String push_title = roomDetailRes.getTitle() + " 경기 ("+")";
            String push_content = "<" + roomDetailRes.getDate() + ">\n" + ownerUser.getName() + "(" +  ownerUser.getNickname() +")";

            // ** 매칭방에 방장이 수락하려는데 매칭 참여인원이 다 찼으면 수락 불가능
            int currentJoinedUser = matchDao.matchRoomJoinedUserCount(matchIdx);
            if (currentJoinedUser >= roomDetailRes.getCount()){
                push_content = "<" + roomDetailRes.getDate() + ">\n"+ "남은 자리가 없습니다.";
                newStatus = "D";
            }

            int pushIdx = pushDao.ownerAccepted(userIdx, joinAcceptOrNotReq, newStatus);

            // 4-1) 수락 시
            if (newStatus == "A"){
                int teamIdx = 0;

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

    // 파이어베이스 서버에 푸쉬 메세지 전송
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

    // 1:1이라고 가정했을 때
    // 매칭 취소 푸쉬 알림
    @Transactional
    public Integer matchCancel(int userIdx, MatchCancelReq matchCancelReq) throws BaseException{
        // 푸쉬알림 전송 메세지 생성 - 방장이 매칭방 취소
        // 푸시알림 보내기
        MatchRoomDetailRes roomDetailRes = matchDao.matchroomDetail(matchCancelReq.getMatchIdx()); // 매칭방 정보

        String push_title = roomDetailRes.getTitle() + " 경기 ("+")";
        String push_content;

        // 1. 매칭취소 유저가 방장인지? 일반유저인지?
        if(userIdx == pushDao.isOwnerCheck(matchCancelReq)){
            // 2. 방장유저라면 -> 매칭방과 매칭방에 속한 유저들을 전부 status D 처리를 해준다.
            pushDao.deleteMatchRoomByOwner(matchCancelReq.getMatchIdx());
            push_content = roomDetailRes.getNickname() + "님이 매칭방을 삭제했습니다.";

        }else{
            // 3. 일반유저라면 -> 매칭방에 속한 상태를 D로 만든다.
            pushDao.exitMatchRoom(userIdx,matchCancelReq.getMatchIdx());
            push_content = roomDetailRes.getNickname() + "님이 매칭을 취소했습니다.";
        }

        int targetUserIdx;
        String targetFcmToken;
        // matchCancelReq.getMatchCancelUserList() 이 리스트가 null 이라면, 매칭방에 참가한 인원이 매칭방장 밖에 없다는 뜻
        // 참여한 사람이 있다는 것은 상대방이 1명 들어왔다는 것. 1:1이라고 온라인을 가정했을때.
        if (matchCancelReq.getMatchCancelUserList() != null){
            targetUserIdx = matchCancelReq.getMatchCancelUserList().get(0).getUserIdx();
            System.out.println(targetUserIdx);
            targetFcmToken = userDao.getTargetFCMtoken(targetUserIdx);
            sendFcmPush(targetFcmToken, push_title, push_content);
        }

        return userIdx == pushDao.isOwnerCheck(matchCancelReq) ? 1 : 0;

    }
}
