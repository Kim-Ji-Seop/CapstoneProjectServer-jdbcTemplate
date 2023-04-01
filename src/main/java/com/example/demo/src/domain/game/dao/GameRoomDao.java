package com.example.demo.src.domain.game.dao;

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

    public PostMatchCodeRes getRoomIdx(PostMatchCodeReq postMatchCodeReq) {
        String query = "select id from match_room where match_code = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new PostMatchCodeRes(
                rs.getInt("id")
        ),postMatchCodeReq.getMatchCode());
    }
}
