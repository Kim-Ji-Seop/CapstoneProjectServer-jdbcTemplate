package com.example.demo.src.domain.match;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.domain.match.dto.ByNetworkRes;
import com.example.demo.src.domain.match.dto.PossibleMatchesRes;
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
    public BaseResponse<List<ByNetworkRes>> getMatchRoomsByNetwork(@RequestParam String network){
        try {
            List<ByNetworkRes> byNetworkRes = matchService.getMatchRoomsByNetwork(network);
            return new BaseResponse<>(byNetworkRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }
    /**
     * Method : GET
     * URI : /rooms/plans
     * Description : 예정 매치
     */

    /**
     * Method : GET
     * URI : /rooms/:matchIdx
     * Description : 매칭방 상세보기
     */


}
