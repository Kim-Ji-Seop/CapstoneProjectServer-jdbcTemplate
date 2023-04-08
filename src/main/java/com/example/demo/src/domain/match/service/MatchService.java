package com.example.demo.src.domain.match.service;

import com.example.demo.config.BaseException;
import com.example.demo.src.domain.history.dao.HistoryDao;
import com.example.demo.src.domain.match.dao.MatchDao;
import com.example.demo.src.domain.match.dto.*;
import com.example.demo.src.domain.user.dao.UserDao;
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
    private final UserDao userDao;
    private final JwtService jwtService;

    public MatchService(MatchDao matchDao, HistoryDao historyDao, UserDao userDao, JwtService jwtService) {
        this.matchDao = matchDao;
        this.historyDao = historyDao;
        this.userDao = userDao;
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
        try{
        List<Integer> matchRecordIdxList = matchDao.getMatchRecord(userIdx);
        HashMap<Integer, List<UserHistoryInfo>> playerOfMatch = new HashMap<>();

        for(int i : matchRecordIdxList){
            int count = matchDao.getMatchRoomPeopleLimit(i);

            if (!playerOfMatch.containsKey(i)){
                if (count == 2){
                    playerOfMatch.put(i, matchDao.getAllUserInfoByMatchIdx(i));
                } else{
                    playerOfMatch.put(i, matchDao.getAllUserInfoByMatchIdxN(i));
                }
                // 위에 두 녀석은 결과값의 길이가 무조건 2로 같다 왜냐하면 팀단위로 뱉기 때문
            }
        }

        int count, homeTeamIdx;
        String network_type, gametime;
        List<HAmatchRecordsRes> hAmatchRecordsRes = new ArrayList<>();
        for (int i: matchRecordIdxList){
            List<UserHistoryInfo> tempUserHistoryInfos = playerOfMatch.get(i);
            count = matchDao.getMatchRoomPeopleLimit(i);
            network_type = matchDao.getMatchRoomNetworkTypeById(i);

            List<MatchRecordsRes> homeNAway = new ArrayList<>();
            UserHistoryInfo result1 = playerOfMatch.get(i).get(0);
            UserHistoryInfo result2 = playerOfMatch.get(i).get(1);
            UserHistoryInfo temp;

            homeTeamIdx = matchDao.getTeamIdx(i, userIdx);
            if(result1.getTeamIdx() != homeTeamIdx){
                temp = result1;
                result1 = result2;
                result2 = temp;
            }

            gametime = matchDao.getGameTime(i);

            homeNAway.add(new MatchRecordsRes(
                    gametime,
                    userDao.userInfo(result1.getUserIdx()).getNickname(),
                    network_type,
                    count,
                    result1.getUserIdx(),
                    result1.getMatchIdx(),
                    result1.getTeamIdx(),
                    "HOME",
                    result1.getSettle_type(),
                    result1.getTotal_score()
            ));
            homeNAway.add(new MatchRecordsRes(
                    gametime,
                    userDao.userInfo(result2.getUserIdx()).getNickname(),
                    network_type,
                    count,
                    result2.getUserIdx(),
                    result2.getMatchIdx(),
                    result2.getTeamIdx(),
                    "AWAY",
                    result2.getSettle_type(),
                    result2.getTotal_score()
            ));
            hAmatchRecordsRes.add(new HAmatchRecordsRes(i, homeNAway));
        }

        return hAmatchRecordsRes;

        }catch (Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public String createMatchCode(int size){
        if(size > 0) {
            char[] tmp = new char[size];
            for(int i=0; i<tmp.length; i++) {
                int div = (int) Math.floor( Math.random() * 2 );

                if(div == 0) { // 0이면 숫자로
                    tmp[i] = (char) (Math.random() * 10 + '0') ;
                }else { //1이면 알파벳
                    tmp[i] = (char) (Math.random() * 26 + 'A') ;
                }
            }
            return new String(tmp);
        }
        return "ERROR : Size is required.";
    }
    public PostCreateMatchRoomRes createMatchRoom(PostCreateMatchRoomReq postCreateMatchRoomReq, int userIdx) throws BaseException{
        try{
            // 1) 매칭 코드 생성
            String matchCode = createMatchCode(6);
            // 2) 매칭방 생성
            PostCreateMatchRoomRes postCreateMatchRoomRes= matchDao.createMatchRoom(postCreateMatchRoomReq,userIdx,matchCode);

            // 3) 매칭방 생성자 플레이어의 참여 목록을 유지하기 위해 history 테이블에 명단식으로 우선 저장
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

    public GetMatchPlanDetailResList matchPlanDetail(int userIdx, int matchIdx) throws BaseException{
        try{
            // 1) 게임시간, 매치코드, 홈/어웨이 유저들의 유저Idx, 팀Idx를 받음
            String game_time = matchDao.getGameTime(matchIdx);
            String match_code = matchDao.getMatchCode(matchIdx);
            int homeTeam = matchDao.getTeamIdx(matchIdx, userIdx);

            // 2) 각 팀의 대표자를 선정 HOME = 유저 본인, AWAY = 상대방 아무나
            List<MatchCandidate> matchCandidates = matchDao.matchCandidates(matchIdx);
            MatchCandidate home = null, away = null;
            for (MatchCandidate candi : matchCandidates){
                if (home != null && away != null){
                    break;
                }
                else if(homeTeam == candi.getTeamIdx() && userIdx == candi.getUserIdx()){
                    home = candi;
                }
                else if (away == null && homeTeam != candi.getTeamIdx()){
                    away = candi;
                }
            }
            System.out.println(home.toString());
            System.out.println(away.toString());

            // 3) 대표자 히스토리 값 생성
            List<GetMatchPlanDetailRes> getMatchPlanDetailRes = new ArrayList<>();
            GetMatchPlanDetailRes homeDetail, awayDetail;

            homeDetail = matchDao.matchPlanDetail(home);
            homeDetail.setHomeOrAway("HOME");
            getMatchPlanDetailRes.add(homeDetail);

            // home은 null인 경우의 수가 없음. 예외처리
            if(away != null) {
                awayDetail = matchDao.matchPlanDetail(away);
                awayDetail.setHomeOrAway("AWAY");
                getMatchPlanDetailRes.add(awayDetail);
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
