package com.capston.bowler.src.domain.game.service;

import com.capston.bowler.config.BaseException;
import com.capston.bowler.src.domain.game.dao.GameRoomDao;
import com.capston.bowler.src.domain.game.dto.*;
import com.capston.bowler.src.domain.match.dao.MatchDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.capston.bowler.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class GameRoomService {

    private final GameRoomDao gameRoomDao;
    private final MatchDao matchDao;

    @Autowired
    public GameRoomService(GameRoomDao gameRoomDao, MatchDao matchDao) {
        this.gameRoomDao = gameRoomDao;
        this.matchDao = matchDao;
    }

    @Transactional
    public PostMatchCodeRes getRoomIdx(PostMatchCodeReq postMatchCodeReq) throws BaseException{
        try{
            // RoomIdx 변경
            int roomIdx = gameRoomDao.getRoomIdx(postMatchCodeReq);
            // 매칭방 상태변경 -> 소켓 활성화(WA)
            gameRoomDao.updateMatchRoomStatus(roomIdx);

            // 매칭방에 들어와있는 유저의 히스토리 Idx와 닉네임
            List<HistoryInfo> historyInfos = gameRoomDao.getHistoryIdxNnick(roomIdx);
            PostMatchCodeRes postMatchCodeRes = new PostMatchCodeRes(roomIdx, historyInfos);

            return postMatchCodeRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostCheckSocketActiveRes getRoomStatus(PostCheckSocketActiveReq postCheckSocketActiveReq) throws BaseException {
        try{
            // 매칭방 상태값 반환
            String status = gameRoomDao.getRoomStatus(postCheckSocketActiveReq);

            // 매칭방에 들어와있는 유저의 히스토리 Idx와 닉네임
            List<HistoryInfo> historyInfos = gameRoomDao.getHistoryIdxNnick(postCheckSocketActiveReq.getMatchIdx());

            return new PostCheckSocketActiveRes(status, historyInfos);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public void matchFinished(List<GameEndReq> gameEndReq) throws BaseException{
        try{
            String settle_type;
            HashMap<Integer, Integer> teamScore = new HashMap<>();
            HashMap<Integer, List<GameEndReq>> teamDto = new HashMap<>();
            int historyIdx = gameEndReq.size() != 0 ? gameEndReq.get(0).getHistoryIdx() : 0;
            int matchIdx = historyIdx != 0 ? matchDao.getMatchIdxFromHistoryIdx(historyIdx): 0;

            // 팀별 점수 합산 - 1팀, 2팀의 총 합이 teamScore 해시맵에 저장됨
            for (GameEndReq gameInfo : gameEndReq){
                int teamIdx = gameRoomDao.getTeamIdx(gameInfo.getHistoryIdx());

                if(!teamScore.containsKey(teamIdx)){
                    teamScore.put(teamIdx, gameInfo.getFrameScores()[9]);

                    List<GameEndReq> teamDtoList = new ArrayList<>();
                    teamDtoList.add(gameInfo);
                    teamDto.put(teamIdx, teamDtoList);
                }
                else{
                    teamScore.put(teamIdx, teamScore.get(teamIdx) + gameInfo.getFrameScores()[9]);
                    teamDto.get(teamIdx).add(gameInfo);
                }
            }
            System.out.println(teamDto.toString());
            System.out.println(teamScore.size());
            // 팀이 두 개만 있다는 가정하에
            int[] teamIdx = new int[2];
            String[] winLose = new String[2];
            if (teamScore.size() == 2){
                Object[] teamIdxlist = teamScore.keySet().toArray();
                teamIdx[0] = (int)teamIdxlist[0];
                teamIdx[1] = (int)teamIdxlist[1];

                // 각 팀의 승, 패, 무 결정
                if (teamScore.get(teamIdx[0]) > teamScore.get(teamIdx[1])) {
                    winLose[0] = "WIN";
                    winLose[1] = "LOSE";
                }
                else if (teamScore.get(teamIdx[0]) < teamScore.get(teamIdx[1])){
                    winLose[0] = "LOSE";
                    winLose[1] = "WIN";
                }
                else{
                    winLose[0] = winLose[1] = "DRAW";
                }

                for (int i = 0; i< 2; i++){
                    for (int j = 0; j < teamDto.get(teamIdx[i]).size(); j++){
                        teamDto.get(teamIdx[i]).get(j).setSettle_type(winLose[i]);
                    }
                }

                for (int teamId: teamDto.keySet()){
                    for (GameEndReq gameInfo: teamDto.get(teamId)){
                        gameRoomDao.updateHistory(gameInfo);
                        for(int frame = 0; frame < 10; frame++){
                            gameRoomDao.updateBowlingScore(
                                    gameInfo.getFrameScoresPerPitch()[frame],
                                    frame+1,
                                    gameInfo.getFrameScores()[frame],
                                    gameInfo.getHistoryIdx());
                        }
                    }
                }
            }

            if (matchIdx != 0){
                matchDao.matchGameOver(matchIdx);
            }

        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
