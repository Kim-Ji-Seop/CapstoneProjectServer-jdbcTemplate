package com.example.demo.src.domain.match.dao;

import com.example.demo.src.domain.match.dto.PossibleMatchesRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MatchDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PossibleMatchesRes countMatches() {
        String query = "select count(*) as cnt " +
                        "from match_room " +
                        "where status='A'";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new PossibleMatchesRes(rs.getInt("cnt")));
    }

}
