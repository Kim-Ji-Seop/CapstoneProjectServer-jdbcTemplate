package com.example.demo.src.domain.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.domain.match.dto.HAmatchRecordsRes;
import com.example.demo.src.domain.match.dto.MatchRecordsRes;
import com.example.demo.src.domain.match.service.MatchService;
import com.example.demo.src.domain.user.dto.*;
import com.example.demo.src.domain.user.service.UserService;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserService userService;

    @Autowired
    private final MatchService matchService;

    @Autowired
    private final JwtService jwtService;

    public UserController(UserService userService, MatchService matchService, JwtService jwtService) {
        this.userService = userService;
        this.matchService = matchService;
        this.jwtService = jwtService;
    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostSignUpRes> signUp(@RequestBody PostSignUpReq postSignUpReq) {
        try {
            PostSignUpRes postSignUpRes = userService.signUp(postSignUpReq);
            return new BaseResponse<>(postSignUpRes);
        }catch (BaseException baseException){
            return new BaseResponse<>((baseException.getStatus()));
        }
    }
    /**
     * 회원가입 > 중복 아이디 체크 API
     * [POST] /users/duplication
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("/duplication")
    public BaseResponse<PostCheckDuplicateRes> checkDuplicateUid(@RequestBody PostCheckDuplicateReq postCheckDuplicateReq) {
        try {
            PostCheckDuplicateRes postCheckDuplicateRes = userService.checkDuplicateUid(postCheckDuplicateReq);
            return new BaseResponse<>(postCheckDuplicateRes);
        }catch (BaseException baseException){
            return new BaseResponse<>((baseException.getStatus()));
        }
    }
    /**
     * 로그인 API
     * [POST] /users/login
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq){
        try {
            PostLoginRes postLoginRes = userService.login(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        }catch (BaseException baseException){
            return new BaseResponse<>((baseException.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /users/login - 테스트용 API
     * @return BaseResponse<PostLoginRes>
     * (BaseResponse 형태가 아닌 Dto 형태 그대로 반환 받기 위함)
     */
    @ResponseBody
    @PostMapping("/login/test")
    public PostLoginRes login_test(@RequestBody PostLoginReq postLoginReq){
        try {
            PostLoginRes postLoginRes = userService.login(postLoginReq);
            return postLoginRes;
        }catch (BaseException baseException){
            PostLoginRes failed = new PostLoginRes(null, 0, null, null);
            return failed;
        }
    }

    /**
     * Method: GET
     * URI: /users/simple-info - 테스트용 API
     * Description: 메인화면 유저 간단 정보
     */
    @ResponseBody
    @GetMapping("/simple-info")
    public BaseResponse<UserSimpleInfo> getMainViewUserInfo() throws BaseException {
        int userIdx = jwtService.getUserIdx();
        try{
            UserSimpleInfo userSimpleInfo = userService.getMainViewUserInfo(userIdx);
            return new BaseResponse<>(userSimpleInfo);
        }catch (BaseException baseException){
            return new BaseResponse<>((baseException.getStatus()));
        }

    }

    /**
     * Method: GET
     * URI: /records
     * Description: 사용자의 전적기록 확인
     *
     */
    @ResponseBody
    @GetMapping("/records")
    public BaseResponse<List<HAmatchRecordsRes>> getMatchRecord() throws BaseException {
        int userIdx = jwtService.getUserIdx();
        //int userIdx = 8;
        try{
            List<HAmatchRecordsRes> matchRecordsRes = matchService.getMatchRecord(userIdx);
            return new BaseResponse<>(matchRecordsRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

}
