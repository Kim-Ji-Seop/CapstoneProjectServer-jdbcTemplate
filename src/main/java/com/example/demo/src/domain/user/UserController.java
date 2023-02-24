package com.example.demo.src.domain.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.domain.user.dto.PostCheckDuplicateReq;
import com.example.demo.src.domain.user.dto.PostCheckDuplicateRes;
import com.example.demo.src.domain.user.dto.PostSignUpReq;
import com.example.demo.src.domain.user.dto.PostSignUpRes;
import com.example.demo.src.domain.user.service.UserService;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
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
}
