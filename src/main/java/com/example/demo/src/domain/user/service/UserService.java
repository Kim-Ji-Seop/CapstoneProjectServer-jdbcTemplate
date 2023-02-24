package com.example.demo.src.domain.user.service;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.domain.user.dao.UserDao;
import com.example.demo.src.domain.user.dto.PostCheckDuplicateReq;
import com.example.demo.src.domain.user.dto.PostCheckDuplicateRes;
import com.example.demo.src.domain.user.dto.PostSignUpReq;
import com.example.demo.src.domain.user.dto.PostSignUpRes;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
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
}
