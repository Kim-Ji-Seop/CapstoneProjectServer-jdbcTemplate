package com.example.demo.src.domain.match.service;

import com.example.demo.config.BaseException;
import com.example.demo.src.domain.history.dao.HistoryDao;
import com.example.demo.src.domain.match.dao.MatchDao;
import com.example.demo.src.domain.match.dto.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class MatchService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MatchDao matchDao;
    private final HistoryDao historyDao;
    private final JwtService jwtService;

    public MatchService(MatchDao matchDao, HistoryDao historyDao, JwtService jwtService) {
        this.matchDao = matchDao;
        this.historyDao = historyDao;
        this.jwtService = jwtService;
    }

    public PossibleMatchesRes countMatches() throws BaseException {
        try{
            return matchDao.countMatches();
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<ByNetworkRes> getMatchRoomsByNetwork(String network) throws BaseException {
        try{
            if(network.equals("ONLINE")) {
                return matchDao.getMatchRoomsOnline(network);
            }else{
                return matchDao.getMatchRoomsOffline(network);
            }
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public MatchRoomDetailRes matchroomDetail(int matchIdx) throws BaseException {
        try{
            return matchDao.matchroomDetail(matchIdx);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<HAmatchRecordsRes> getMatchRecord(int userIdx) throws BaseException{
        List<HAmatchRecordsRes> hAmatchRecordsRes = new ArrayList<>();
        List<MatchRecordsRes> matchRecordsRes = matchDao.getMatchRecord(userIdx);
        try{
            for (int i =0; i<matchRecordsRes.size(); i+=2 ){
                // 이렇게 2개씩 묶을 수 있는 이유는 DB에서 ORDER BY 로 매칭방 번호순으로 쿼리를 뱉어냈기 때문
                // 2개의 연속된 매칭방 번호를 붙여서 리스트로 반환 받았음.
                List<MatchRecordsRes> homeNaway = new ArrayList<>();
                MatchRecordsRes result1 = matchRecordsRes.get(i);
                MatchRecordsRes result2 = matchRecordsRes.get(i+1);
                int matchIdx1 = result1.getMatchIdx();
                int matchIdx2 = result2.getMatchIdx();

                if (matchIdx1 == matchIdx2) { // 매칭방 번호가 같을때
                    if(result2.getUserIdx() == userIdx){
                        MatchRecordsRes temp = result1;
                        result1 = result2;
                        result2 = temp;
                    }
                    result1.setHomeOrAway("HOME");
                    result2.setHomeOrAway("AWAY");
                    homeNaway.add(result1);
                    homeNaway.add(result2);
                }
                else{
                    throw new BaseException(DATABASE_ERROR);
                }
                hAmatchRecordsRes.add(new HAmatchRecordsRes(matchIdx1, homeNaway));

            }
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
        return hAmatchRecordsRes;
    }

    public PostCreateMatchRoomRes createMatchRoom(PostCreateMatchRoomReq postCreateMatchRoomReq, int userIdx) throws BaseException{
        try{
            // 1) 매칭방 생성
            PostCreateMatchRoomRes postCreateMatchRoomRes= matchDao.createMatchRoom(postCreateMatchRoomReq,userIdx);

            // 2) 매칭방 생성자 플레이어의 참여 목록을 유지하기 위해 history 테이블에 명단식으로 우선 저장
            historyDao.createMatchRoomNewPlayer(userIdx, postCreateMatchRoomRes.getMatchIdx(), 1);

            return postCreateMatchRoomRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetMatchPlanResList> matchPlanList(int userIdx) throws BaseException{
        try{
            List<GetMatchPlanRes> getMatchPlanRes = matchDao.matchPlanList(userIdx);
            HashMap<Integer, List> matchPlan_hasyByMatchIdx = new HashMap<Integer, List>();

            int parsingMatchIdx;
            for (GetMatchPlanRes matchplan : getMatchPlanRes){
                parsingMatchIdx = matchplan.getMatchIdx();

                if (!matchPlan_hasyByMatchIdx.containsKey(parsingMatchIdx)){
                    matchPlan_hasyByMatchIdx.put(parsingMatchIdx, new ArrayList<>());
                }
                if (matchplan.getHomeOrAway().equals("HOME")){
                    matchPlan_hasyByMatchIdx.get(parsingMatchIdx).add(0, matchplan);
                }
                else{
                    matchPlan_hasyByMatchIdx.get(parsingMatchIdx).add(matchplan);
                }
            }

            List<GetMatchPlanResList> getMatchPlanResLists = new ArrayList<>();

            for(Integer key: matchPlan_hasyByMatchIdx.keySet()){
                int matchIdx = key;
                GetMatchPlanRes get0 = ((GetMatchPlanRes) matchPlan_hasyByMatchIdx.get(key).get(0));

                getMatchPlanResLists.add(new GetMatchPlanResList(
                        key,
                        get0.getGame_time(),
                        get0.getNetwork_type(),
                        matchPlan_hasyByMatchIdx.get(key)));
            }

            return getMatchPlanResLists;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetMatchPlanDetailResList matchPlanDetial(int userIdx, int matchIdx) throws BaseException{
        try{
            String game_time = matchDao.getGameTime(matchIdx);
            String match_code = matchDao.getMatchCode(matchIdx);
            int homeTeam = matchDao.getTeamIdx(matchIdx, userIdx);
            List<GetMatchPlanDetailRes> getMatchPlanDetailRes = new ArrayList<>();

            for (GetMatchPlanDetailRes planDetail : matchDao.matchPlanDetial(userIdx, matchIdx)){
                if (planDetail.getTeamIdx() == homeTeam){
                    planDetail.setHomeOrAway("HOME");
                    getMatchPlanDetailRes.add(0, planDetail);
                }
                else{
                    planDetail.setHomeOrAway("AWAY");
                    getMatchPlanDetailRes.add(planDetail);
                }
            }

            GetMatchPlanDetailResList getMatchPlanDetailResListList = new GetMatchPlanDetailResList(
                    matchIdx,
                    game_time,
                    match_code == null ? "": match_code,
                    getMatchPlanDetailRes
            );

            return getMatchPlanDetailResListList;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
