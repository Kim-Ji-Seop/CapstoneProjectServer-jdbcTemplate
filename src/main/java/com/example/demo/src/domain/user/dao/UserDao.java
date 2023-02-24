package com.example.demo.src.domain.user.dao;

import com.example.demo.src.domain.user.dto.PostSignUpReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 회원가입 관련 쿼리
     */
    // 중복 아이디 체크
    public int checkUid(String uid) {
        String query = "select exists(select id from user where uid = ? and status = 'A')";
        return this.jdbcTemplate.queryForObject(query, int.class, uid);
    }

    // 중복 닉네임 체크
    public int checkNickName(String nickName) {
        String query = "select exists(select id from user where nickname = ? and status = 'A')";
        return this.jdbcTemplate.queryForObject(query, int.class, nickName);
    }

    // 회원가입 -> 마지막 insert PK ID return
    public int signUp(PostSignUpReq postSignUpReq) {
        String query = "insert into user (uid,password,name,nickname) values (?,?,?,?)";
        Object[] createUserParams = new Object[]{
                postSignUpReq.getUid(),
                postSignUpReq.getPassword(),
                postSignUpReq.getName(),
                postSignUpReq.getNickName()
        };
        this.jdbcTemplate.update(query, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }
}
