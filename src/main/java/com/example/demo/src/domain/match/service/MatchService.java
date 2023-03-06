package com.example.demo.src.domain.match.service;

import com.example.demo.config.BaseException;
import com.example.demo.src.domain.match.dao.MatchDao;
import com.example.demo.src.domain.match.dto.ByNetworkRes;
import com.example.demo.src.domain.match.dto.MatchRoomDetailRes;
import com.example.demo.src.domain.match.dto.PossibleMatchesRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
            if(network.equals("온라인")) {
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
}