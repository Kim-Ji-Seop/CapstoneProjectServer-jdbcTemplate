package com.capston.bowler.src.domain.game.dao;

import com.capston.bowler.src.domain.game.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class GameRoomDao {
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // 매칭 RoomIdx 반환
    public Integer getRoomIdx(PostMatchCodeReq postMatchCodeReq) {
        String query = "select id from match_room where match_code = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new Integer(
                rs.getInt("id")
        ),postMatchCodeReq.getMatchCode());
    }
    //
    public void updateMatchRoomStatus(int roomIdx) {
        String query = "update match_room set status = 'WA' where id = ?";
        this.jdbcTemplate.update(query, roomIdx);
    }

    public List<HistoryInfo> getHistoryIdxNnick(int roomIdx){
        String query ="SELECT\n" +
                "    h.id as historyIdx, u.nickname\n" +
                "FROM history h\n" +
                "LEFT JOIN user u ON h.userIdx = u.id\n" +
                "WHERE matchIdx = ?";
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new HistoryInfo(
                        rs.getInt("historyIdx"),
                        rs.getString("nickname")
                ), roomIdx);
    }

    public String getRoomStatus(PostCheckSocketActiveReq postCheckSocketActiveReq) {
        String query = "select status from match_room where id = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getString("status")
                ,postCheckSocketActiveReq.getMatchIdx());
    }

    public int getTeamIdx(int historyIdx){
        String query = "SELECT teamIdx FROM history " +
                "WHERE id =?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getInt("teamIdx")
                , historyIdx
        );
    }

    public void updateHistory(GameEndReq gameEndReq) {
        String query = "UPDATE history\n" +
                "    SET total_score = ?,\n" +
                "        settle_type = ?\n" +
                "WHERE id = ?\n";

        Object[] param = {gameEndReq.getFrameScores()[9],
                gameEndReq.getSettle_type(),
                gameEndReq.getHistoryIdx()
        };


        this.jdbcTemplate.update(query, param);
    }

    public void updateBowlingScore(int[] pitchScore, int frame_number, int accumulate_score, int match_userIdx) {
        String query = "INSERT INTO bowling_score " +
                "(first_pitch, second_pitch, third_pitch, frame_number, accumulate_score, match_userIdx, score_type)\n" +
                "VALUES\n" +
                "    (?, ?, ?, ?, ?, ?, ?)";

        String score_type = "NORMAL";
        if(pitchScore[0] == 10){
            score_type = "STRIKE";
        }
        else if(pitchScore[0] + pitchScore[1] == 10){
            score_type = "SPARE";
        }

        Object[] param = {
                pitchScore[0],
                pitchScore[1] == -1? 0: pitchScore[1],
                pitchScore[2] == -1? 0: pitchScore[2],
                frame_number,
                accumulate_score,
                match_userIdx,
                score_type
        };

        this.jdbcTemplate.update(query, param);
    }
}
