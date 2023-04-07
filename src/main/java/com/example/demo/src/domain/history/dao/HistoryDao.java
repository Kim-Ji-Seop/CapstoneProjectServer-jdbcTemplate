package com.example.demo.src.domain.history.dao;

import com.example.demo.src.domain.history.dto.NewHistoryPlayerRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class HistoryDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public NewHistoryPlayerRes createMatchRoomNewPlayer(int userIdx, int matchIdx, int teamIdx) {
        // 1. 히스토리 테이블에서 매칭방에 참가 유저 추가
        String newPlayerQuery = "INSERT INTO history (userIdx, matchIdx, teamIdx)" +
                "VALUES (?, ?, ?)";
        Object[] historyParam = new Object[]{
                userIdx,
                matchIdx,
                teamIdx
        };

        this.jdbcTemplate.update(newPlayerQuery, historyParam);

        String lastInsertedQ = "select last_insert_id()";
        int newPlayerNum = this.jdbcTemplate.queryForObject(lastInsertedQ, int.class);
        return new NewHistoryPlayerRes(newPlayerNum, matchIdx, userIdx, teamIdx);
    }

    public int getUserGameCount(int userIdx){
        String query = "SELECT\n" +
                "    COUNT(*) as gameCount\n" +
                "FROM history\n" +
                "WHERE userIdx = ? AND total_score IS NOT null";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getInt("gameCount")
                , userIdx);
    }

    public int getWinCount(int userIdx) {
        String query = "SELECT\n" +
                "    COUNT(id) as winCount\n" +
                "FROM history\n" +
                "WHERE userIdx= ? AND total_score IS NOT NULL AND settle_type = 'WIN'";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getInt("winCount")
                , userIdx);
    }

    public int getLoseCount(int userIdx) {
        String query = "SELECT\n" +
                "    COUNT(id) as loseCount\n" +
                "FROM history\n" +
                "WHERE userIdx= ? AND total_score IS NOT NULL AND settle_type = 'LOSE'";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getInt("loseCount")
                , userIdx);
    }

    public int getDrawCount(int userIdx) {
        String query = "SELECT\n" +
                "    COUNT(id) as drawCount\n" +
                "FROM history\n" +
                "WHERE userIdx= ? AND total_score IS NOT NULL AND settle_type = 'DRAW'";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getInt("drawCount")
                , userIdx);
    }

    public int getAvgScore(int userIdx) {
        String query = "SELECT\n" +
                "    ROUND(AVG(total_score)) as avgScore\n" +
                "FROM history\n" +
                "WHERE userIdx = ? AND total_score IS NOT NULL";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getInt("avgScore")
                , userIdx);
    }

    public int getHighScore(int userIdx) {
        String query = "SELECT\n" +
                "    MAX(total_score) as highScore\n" +
                "FROM history\n" +
                "WHERE userIdx = ? AND total_score IS NOT NULL";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getInt("highScore")
                , userIdx);
    }

    public List<Integer> getHistoryIdxes(int userIdx) {
        String query = "SELECT\n" +
                "    id\n" +
                "FROM history\n" +
                "WHERE userIdx= ? AND total_score IS NOT NULL";

        return this.jdbcTemplate.query(query,
                ((rs, rowNum) -> rs.getInt("id")
                ), userIdx);
    }

    public int getStrikeCount(int historyIdx){
        String query = "SELECT\n" +
                "    COUNT(score_type) as strikeCount\n" +
                "FROM bowling_score\n" +
                "WHERE match_userIdx = ? AND SCORE_TYPE = 'STRIKE'";
        return this.jdbcTemplate.queryForObject(query,
                ((rs, rowNum) -> rs.getInt("strikeCount")
                ), historyIdx);
    }


}
