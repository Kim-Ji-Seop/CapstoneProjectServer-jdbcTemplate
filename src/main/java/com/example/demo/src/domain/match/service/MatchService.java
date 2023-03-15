package com.example.demo.src.domain.match.service;

import com.example.demo.config.BaseException;
import com.example.demo.src.domain.match.dao.MatchDao;
import com.example.demo.src.domain.match.dto.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class MatchService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MatchDao matchDao;
    private final JwtService jwtService;

    public MatchService(MatchDao matchDao, JwtService jwtService) {
        this.matchDao = matchDao;
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

            return matchDao.createMatchRoom(postCreateMatchRoomReq,userIdx);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
