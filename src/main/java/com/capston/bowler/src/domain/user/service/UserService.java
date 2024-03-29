package com.capston.bowler.src.domain.user.service;

import com.capston.bowler.config.BaseException;
import com.capston.bowler.config.secret.Secret;
import com.capston.bowler.src.domain.history.dao.HistoryDao;
import com.capston.bowler.src.domain.match.dao.MatchDao;
import com.capston.bowler.src.domain.user.dao.UserDao;
import com.capston.bowler.src.domain.user.dto.*;
import com.capston.bowler.utils.AES128;
import com.capston.bowler.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.capston.bowler.config.BaseResponseStatus.*;
import static com.capston.bowler.utils.ValidationRegex.*;

@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final MatchDao matchDao;

    private final HistoryDao historyDao;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserDao userDao, MatchDao matchDao, HistoryDao historyDao, JwtService jwtService) {
        this.userDao = userDao;
        this.matchDao = matchDao;
        this.historyDao = historyDao;
        this.jwtService = jwtService;
    }

    // 회원가입
    @Transactional
    public PostSignUpRes signUp(PostSignUpReq postSignUpReq) throws BaseException {
        // 아이디, 비밀번호, 닉네임 정규식 처리
        if(postSignUpReq.getUid().length() == 0 || postSignUpReq.getPassword().length() == 0 || postSignUpReq.getNickName().length() == 0 || postSignUpReq.getName().length() == 0){
            throw new BaseException(REQUEST_ERROR); // 2000 : 입력값 전체 빈 값일때
        }
        if(!isRegexUid(postSignUpReq.getUid())){
            throw new BaseException(POST_USERS_INVALID_UID); // 2010 : 아이디 정규 표현식 예외
        }
        if(!isRegexPassword(postSignUpReq.getPassword())){
            throw new BaseException(POST_USERS_INVALID_PASSWORD); // 2011 : 비밀번호 정규 표현식 예외
        }
        if(!isRegexNickName(postSignUpReq.getNickName())){
            throw new BaseException(POST_USERS_INVALID_NICK_NAME); // 2012 : 닉네임 정규 표현식 예외
        }
        // 중복 아이디 체크
        if(userDao.checkUid(postSignUpReq.getUid()) == 1){
            throw new BaseException(POST_USERS_EXISTS_ID); // 2018 : 중복 아이디
        }
        // 중복 닉네임 체크
        if(userDao.checkNickName(postSignUpReq.getNickName()) == 1){
            throw new BaseException(POST_USERS_EXISTS_NICK_NAME); // 2019 : 중복 닉네임
        }
        try{
            // 비밀번호 암호화
            String pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postSignUpReq.getPassword()); // 비밀번호 암호화
            postSignUpReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR); // 4011 : 비밀번호 암호화에 실패하였습니다
        }
        try{
            // 유저 고유식별번호
            int userIdx = userDao.signUp(postSignUpReq);
            return new PostSignUpRes(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostCheckDuplicateRes checkDuplicateUid(PostCheckDuplicateReq postCheckDuplicateReq) throws BaseException {
        // 중복 아이디 체크
        if(userDao.checkUid(postCheckDuplicateReq.getUid()) == 1){
            return new PostCheckDuplicateRes(0); // 중복된 아이디
        }else {
            return new PostCheckDuplicateRes(1); // 가능한 아이디
        }
    }

    // 로그인
    @Transactional
    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException {
        if(postLoginReq.getUid().length() == 0 || postLoginReq.getPassword().length() == 0){
            throw new BaseException(REQUEST_ERROR); // 2000 : 입력값 전체 빈 값일때
        }

        String pwd;

        try{
            // 비밀번호 암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postLoginReq.getPassword()); // 비밀번호 암호화
            postLoginReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR); // 4011 : 비밀번호 암호화에 실패하였습니다
        }

        User user = userDao.login(postLoginReq);

        // uid & pwd 일치 여부
        if(user.getUid().equals(postLoginReq.getUid()) && user.getPassword().equals(pwd)){
            int userIdx = user.getUserIdx();
            userDao.loginTokenUpdate(user.getUid(), user.getPassword(), postLoginReq.getToken());
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(jwt,userIdx,user.getName(),user.getNickname());
        }else{
            throw new BaseException(FAILED_TO_LOGIN); // 3014 : 없는 아이디거나 비밀번호가 틀렸습니다.
        }
    }

    public UserSimpleInfo getMainViewUserInfo(int userIdx) throws BaseException {
        try {
            UserNameNickName userInfo = userDao.userInfo(userIdx);
            int recentAvgScore = historyDao.getRecentAvgScore(userIdx);
            int recentWinCount = historyDao.getRecentWinCount(userIdx);
            int recentLoseCount = historyDao.getRecentLoseCount(userIdx);
            int recentDrawCount = historyDao.getRecentDrawCount(userIdx);
            float maxTenGame = 10.0f;

            if (recentWinCount + recentLoseCount + recentDrawCount < 10){
                maxTenGame = recentWinCount + recentLoseCount + recentDrawCount;
            }

            int recentWinRate = (int) (recentWinCount / maxTenGame  * 100);

            return new UserSimpleInfo(
                    userInfo.getName(),
                    userInfo.getNickname(),
                    recentAvgScore,
                    recentWinCount,
                    recentLoseCount,
                    recentDrawCount,
                    recentWinRate
                    );
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetPushListResByDateArr> getPushRecord(int userIdx) throws BaseException{
        try{
            // 1) 푸쉬 알림 리스트 목록 가져오기
            List<GetPushListRes> pushList =  userDao.getPushRecord(userIdx);
            HashMap<String, List> pushList_hashByDate = new HashMap<>(); // 날짜별 리스트

            // 2) 알림 생성 날짜별 리스트 묶기
            String parsingDate;
            for (GetPushListRes push : pushList){
                // 2-1) 해쉬맵 형태로 날짜 - 키, 푸쉬 리스트- 밸류 형태로 파싱
                parsingDate = push.getOnlydate();
                if (!pushList_hashByDate.containsKey(parsingDate)){
                    pushList_hashByDate.put(parsingDate, new ArrayList<>());
                }

                // 2-2) 유저 프로필 이미지 링크, 유저 닉네임 값 가져오기
                GetUserProfileImgRes userProfileImgRes;
                UserNameNickName userNameNickName;
                if(push.getOwner_userIdx() == userIdx){
                    userProfileImgRes = userDao.getUserProfileImg(push.getJoin_userIdx());
                    userNameNickName = userDao.userInfo(push.getJoin_userIdx());
                }
                else if(push.getJoin_userIdx() == userIdx){
                    userProfileImgRes = userDao.getUserProfileImg(push.getOwner_userIdx());
                    userNameNickName = userDao.userInfo(push.getOwner_userIdx());
                }
                else{
                    throw new BaseException(DATABASE_ERROR);
                }
                push.setProfileImg_url(userProfileImgRes.getUserProfileImgUrl());
                push.setOpponentNick(userNameNickName.getNickname());

                // 2-3) 날짜 키에 리스트 해당 푸쉬 정보를 추가하기.
                if (pushList_hashByDate.containsKey(parsingDate)){
                    pushList_hashByDate.get(parsingDate).add(push);
                }
            }

            // 3) 분류된 날짜별 해쉬맵에 따라서 반환 JSON 값 생성
            List<GetPushListResByDateArr> pushListRes = new ArrayList<>();
            // 3-1) key set 정렬
            List<String> sortedKeySet = new ArrayList<>(pushList_hashByDate.keySet());
            sortedKeySet.sort((s1, s2) -> s2.compareTo(s1));

            for (String key: sortedKeySet){
                pushListRes.add(new GetPushListResByDateArr(key, pushList_hashByDate.get(key)));
            }
            return pushListRes;

        }catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // User 의 프로필 정보 가져오기
    public UserProfileInfo getUserProfileInfo(int userIdx) {
        // 서브쿼리 사용을 하지않고 SQL 분리
        UserNameNickName userNameNickName = userDao.userInfo(userIdx);
        int userGameCount = historyDao.getUserGameCount(userIdx); // user 게임 수
        int userWinCount = historyDao.getWinCount(userIdx); // user 이긴 횟수
        int userLoseCount = historyDao.getLoseCount(userIdx); // user 진 횟수
        int userDrawCount = historyDao.getDrawCount(userIdx); // user 비긴 횟수
        int userAvgScore = historyDao.getAvgScore(userIdx); // user 평균 점수
        int userHighScore = historyDao.getHighScore(userIdx); // user 최고 점수

        // 유저의 히스토리 게임 기록 가져오기
        List<Integer> historyGames = historyDao.getHistoryIdxes(userIdx);

        // 스트라이크 수 가져오기
        int totalStrikeCount = 0;
        for (int historyIdx : historyGames){
            totalStrikeCount += historyDao.getStrikeCount(historyIdx);
        }

        // 스트라이크율 계산
        double strikeRate = totalStrikeCount == 0 ? 0 : ((double) totalStrikeCount / (10 * userGameCount)) * 100;
        strikeRate = Math.round(strikeRate * 100) / 100.0;

        UserProfileInfo userProfileInfo = new UserProfileInfo(
                userNameNickName.getName(),
                userNameNickName.getNickname(),
                userWinCount,
                userLoseCount,
                userDrawCount,
                userAvgScore,
                userHighScore,
                strikeRate,
                userGameCount
        );

        return userProfileInfo;

    }
}
