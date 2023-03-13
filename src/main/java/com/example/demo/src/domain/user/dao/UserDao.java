package com.example.demo.src.domain.user.dao;

import com.example.demo.src.domain.user.dto.PostLoginReq;
import com.example.demo.src.domain.user.dto.PostSignUpReq;
import com.example.demo.src.domain.user.dto.User;
import com.example.demo.src.domain.user.dto.UserSimpleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
        String query = "insert into user (uid,password,name,nickname, devicetoken) values (?,?,?,?,?)";
        Object[] createUserParams = new Object[]{
                postSignUpReq.getUid(),
                postSignUpReq.getPassword(),
                postSignUpReq.getName(),
                postSignUpReq.getNickName(),
                postSignUpReq.getToken()
        };
        this.jdbcTemplate.update(query, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    // 로그인
    public User login(PostLoginReq postLoginReq) {
        String query = "select id,uid,password,name,nickname from user where uid = ? and password = ?";
        Object[] logInParams = new Object[]{ postLoginReq.getUid(), postLoginReq.getPassword() };

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new User(
                        rs.getInt("id"),
                        rs.getString("uid"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("nickname")
                ), logInParams// RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        ); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    public UserSimpleInfo userInfo(int userIdx){
        String query = "select `name`, nickname from user where id=?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new UserSimpleInfo(
                        rs.getString("name"),
                        rs.getString("nickname")
                ), userIdx);
    }


    public String getTargetFCMtoken(int targetUserIdx) {
        String query = "select devicetoken from user where id =?";

//        return this.jdbcTemplate.queryForObject(query,
//                (rs, rowNum) -> (
//                        rs.getString("devicetoken")
//                ), targetUserIdx);
        try {
            return this.jdbcTemplate.queryForObject(query,
                    (rs, rowNum) -> (
                            rs.getString("devicetoken")
                    ), targetUserIdx);
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);

        }
        return null;
    }
}
