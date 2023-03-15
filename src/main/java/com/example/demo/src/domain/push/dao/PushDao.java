package com.example.demo.src.domain.push.dao;

import com.example.demo.src.domain.push.dto.MatchJoinPushRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class PushDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int sendPush(int targetUserIdx, int userIdx, int matchIdx, String push_title, String push_content){
        String query = "insert into push (owner_userIdx, join_userIdx, matchIdx, push_title, push_content) values (?, ?, ?, ?, ?)";
        Object[] sendPushParams = new Object[] {
                targetUserIdx,
                userIdx,
                matchIdx,
                push_title,
                push_content
        };
        this.jdbcTemplate.update(query, sendPushParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }
}
