package com.example.demo.src.domain.history.dao;

import com.example.demo.src.domain.history.dto.NewHistoryPlayerRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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

}
