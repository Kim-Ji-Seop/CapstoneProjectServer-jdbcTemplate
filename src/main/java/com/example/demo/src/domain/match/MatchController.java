package com.example.demo.src.domain.match;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.domain.match.dto.*;
import com.example.demo.src.domain.match.service.MatchService;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/matches")
public class MatchController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final MatchService matchService;
    @Autowired
    private final JwtService jwtService;

    public MatchController(MatchService matchService, JwtService jwtService) {
        this.matchService = matchService;
        this.jwtService = jwtService;
    }
    /**
     * Method : GET
     * URI : /rooms/counts
     * Description : 참여가능 매치 총 갯수
     */
    @ResponseBody
    @GetMapping("/rooms/counts")
    public BaseResponse<PossibleMatchesRes> countMatches(){
        try {
            PossibleMatchesRes possibleMatchesRes = matchService.countMatches();
            return new BaseResponse<>(possibleMatchesRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/rooms/counts/online")
    public BaseResponse<PossibleMatchesRes> onlineCountMatches(){
        try {
            PossibleMatchesRes possibleMatchesRes = matchService.onlineCountMatches();
            return new BaseResponse<>(possibleMatchesRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


    /**
     * Method : GET
     * URI : /rooms/locations
     * Description : 지역별 매치
     */

    /**
     * Method : GET
     * URI : /rooms?network=
     * Description : 온/오프라인 매치
     */
    @ResponseBody
    @GetMapping("/rooms")
    public BaseResponse<List<ByNetworkRes>> getMatchRoomsByNetwork(@RequestParam String network,
                                                                   @RequestParam(required = false) String localName,
                                                                   @RequestParam(required = false) String cityName){
        try {
            System.out.println(network + " " + localName + " " + cityName + " ");
            List<ByNetworkRes> byNetworkRes = matchService.getMatchRoomsByNetwork(network, localName, cityName);
            return new BaseResponse<>(byNetworkRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }


    /**
     * Method : GET
     * URI : /rooms/:matchIdx
     * Description : 매칭방 상세보기
     */
    @ResponseBody
    @GetMapping("/rooms/{matchIdx}")
    public BaseResponse<MatchRoomDetailRes> matchroomDetail(@PathVariable("matchIdx") int matchIdx){
        try {
            MatchRoomDetailRes matchRoomDetailRes = matchService.matchroomDetail(matchIdx);
            return new BaseResponse<>(matchRoomDetailRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * Method : GET
     * URI : /rooms/:matchIdx/test
     * Description : 매칭방 상세보기 - 테스트용 API
     * (BaseResponse 형태가 아닌 Dto 형태 그대로 반환 받기 위함)
     */
    @ResponseBody
    @GetMapping("/rooms/{matchIdx}/test")
    public MatchRoomDetailRes matchroomDetail_test(@PathVariable("matchIdx") int matchIdx){
        try {
            MatchRoomDetailRes matchRoomDetailRes = matchService.matchroomDetail(matchIdx);
            return matchRoomDetailRes;
        }catch (BaseException baseException){
            return new MatchRoomDetailRes(null, null, null, null, 0, 0, 0, null, null, 0);
        }
    }


    /**
     * Method: POST
     * URI: /rooms
     * Description: 매칭방 개설하기
     */
    @ResponseBody
    @PostMapping("/rooms")
    public BaseResponse<PostCreateMatchRoomRes> createMatchRoom(@RequestBody PostCreateMatchRoomReq postCreateMatchRoomReq){
        try{
            int userIdx = jwtService.getUserIdx();

            PostCreateMatchRoomRes postCreateMatchRoomRes = matchService.createMatchRoom(postCreateMatchRoomReq,userIdx);
            return new BaseResponse<>(postCreateMatchRoomRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * Method: POST
     * URI: /rooms/test - 테스트용 API
     * Description: 매칭방 개설하기
     * (BaseResponse 형태가 아닌 Dto 형태 그대로 반환 받기 위함)
     */
    @ResponseBody
    @PostMapping("/rooms/test")
    public PostCreateMatchRoomRes createMatchRoom_test(@RequestBody PostCreateMatchRoomReq postCreateMatchRoomReq){
        try{
            int userIdx = jwtService.getUserIdx();
            PostCreateMatchRoomRes postCreateMatchRoomRes = matchService.createMatchRoom(postCreateMatchRoomReq,userIdx);
            return postCreateMatchRoomRes;
        }catch (BaseException baseException){
            PostCreateMatchRoomRes failed = new PostCreateMatchRoomRes(0);
            return failed;
        }
    }

    /**
     * Method : GET
     * URI : /rooms/plans
     * Description : 예정 매치
     */
    @ResponseBody
    @GetMapping("/rooms/plans")
    public BaseResponse<List<GetMatchPlanResList>> matchPlanList(){
        try{
            int userIdx = jwtService.getUserIdx();
            List<GetMatchPlanResList> getMatchPlanResList = matchService.matchPlanList(userIdx);
            return new BaseResponse<>(getMatchPlanResList);
        }catch(BaseException baseException){
            System.out.println(baseException);
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * Method : GET
     * URI : /rooms/plans/:matchIdx
     * Description : 예정 매치 상세
     */
    @ResponseBody
    @GetMapping("/rooms/plans/{matchIdx}")
    public BaseResponse<GetMatchPlanDetailResList> matchPlanDetial(@PathVariable int matchIdx){
        try{
            int userIdx = jwtService.getUserIdx();
            GetMatchPlanDetailResList getMatchPlanDetailRes = matchService.matchPlanDetail(userIdx, matchIdx);
            return new BaseResponse<>(getMatchPlanDetailRes);
        }catch(BaseException baseException){
            System.out.println(baseException);
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/local")
    public BaseResponse<List<String>> getLocalCities(@RequestParam String localName){
        try{
            return new BaseResponse<>(matchService.getLocalCities(localName));
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }

    }



}
