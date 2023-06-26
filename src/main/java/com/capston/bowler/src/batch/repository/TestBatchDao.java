package com.capston.bowler.src.batch.repository;

import com.capston.bowler.src.batch.domain.TestBatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class TestBatchDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int countAllByStatus(String status){
        String query = "select count(status) as c from test_batch where status = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs,rowNum) -> new Integer(rs.getInt("c")
                ), status);
    }

    public List<TestBatch> findAllByStatus(String status){
        String query ="select * from test_batch where status = ?";
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new TestBatch(
                        rs.getLong("id"),
                        rs.getString("status"),
                        rs.getString("created"),
                        rs.getString("updated")
                ), status);
    }

    public void changeStatus(Long id){
        String query = "UPDATE test_batch " +
                "SET status = ? " +
                "WHERE id = ?";

        Object[] param = {"D", id};
        this.jdbcTemplate.update(query, param);
    }

    public void changeAndInsert(){
        String query ="INSERT INTO test_batch (status) " +
                "VALUES ('A'), ('A'), ('A'), ('A'), ('A')";
        this.jdbcTemplate.update(query);
    }


}
