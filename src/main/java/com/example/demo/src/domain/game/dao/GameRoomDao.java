package com.example.demo.src.domain.game.dao;

import com.example.demo.src.domain.game.dto.PostCheckSocketActiveReq;
import com.example.demo.src.domain.game.dto.PostCheckSocketActiveRes;
import com.example.demo.src.domain.game.dto.PostMatchCodeReq;
import com.example.demo.src.domain.game.dto.PostMatchCodeRes;
import com.example.demo.src.domain.match.dto.MatchRoomDetailRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class GameRoomDao {
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // 매칭 RoomIdx 반환
    public PostMatchCodeRes getRoomIdx(PostMatchCodeReq postMatchCodeReq) {
        String query = "select id from match_room where match_code = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new PostMatchCodeRes(
                rs.getInt("id")
        ),postMatchCodeReq.getMatchCode());
    }
    //
    public void updateMatchRoomStatus(PostMatchCodeRes postMatchCodeRes) {
        String query = "update match_room set status = 'WA' where id = ?";
        this.jdbcTemplate.update(query,postMatchCodeRes.getRoomIdx());
    }

    public PostCheckSocketActiveRes getRoomStatus(PostCheckSocketActiveReq postCheckSocketActiveReq) {
        String query = "select status from match_room where id = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new PostCheckSocketActiveRes(
                        rs.getString("status")
                ),postCheckSocketActiveReq.getMatchIdx());
    }
}
