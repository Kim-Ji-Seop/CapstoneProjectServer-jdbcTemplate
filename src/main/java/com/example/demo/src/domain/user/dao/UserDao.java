package com.example.demo.src.domain.user.dao;

import com.example.demo.src.domain.user.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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

    public void loginTokenUpdate(String uid, String pwdEncrypted, String devicetoken){
        String query = "UPDATE user SET devicetoken = ?\n" +
                "WHERE uid = ? and password = ?";
        Object[] logInParams = new Object[]{ devicetoken, uid, pwdEncrypted };
        System.out.println(pwdEncrypted + " "+  devicetoken);
        this.jdbcTemplate.update(query, logInParams);
    }

    public UserNameNickName userInfo(int userIdx){
        String query = "select `name`, nickname from user where id=?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new UserNameNickName(
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

    public UserSimpleInfo getMainViewUserInfo(int userIdx) {
        String query = "SELECT u.name, u.nickname,\n" +
                "       ROUND(h.total_score) as average,\n" +
                "       win_count, lose_count, draw_count,\n" +
                "       ROUND(h.win_count / h.tenofN * 100) as win_Rate\n" +
                "FROM (SELECT COUNT(userIdx) as tenofN,\n" +
                "        userIdx, avg(total_score) as total_score,\n" +
                "          COUNT(case when settle_type = 'WIN' then 1 end) as win_count,\n" +
                "          COUNT(case when settle_type = 'LOSE' then 1 end) as lose_count,\n" +
                "          COUNT(case when settle_type = 'DRAW' then 1 end) as draw_count\n" +
                "        FROM history\n" +
                "      WHERE userIdx = ?) as h\n" +
                "LEFT JOIN user u on u.id = h.userIdx";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new UserSimpleInfo(
                        rs.getString("name"),
                        rs.getString("nickname"),
                        rs.getInt("average"),
                        rs.getInt("win_count"),
                        rs.getInt("lose_count"),
                        rs.getInt("draw_count"),
                        rs.getInt("win_Rate")
                ), userIdx);
    }

    public List<GetPushListRes> getPushRecord(int userIdx) {
        String query = "SELECT p.id, p.owner_userIdx, p.join_userIdx, p.matchIdx,\n" +
                "       p.push_title, p.push_content,\n" +
                "       mr.game_time, mr.network_type,\n" +
                "       p.updated, DATE_FORMAT(p.updated, '%Y-%m-%d') as onlydate,\n" +
                "       p.status\n" +
                "FROM push p\n" +
                "LEFT JOIN match_room mr on mr.id = p.matchIdx\n" +
                "WHERE p.owner_userIdx = ? or p.join_userIdx = ?\n" +
                "ORDER BY p.updated DESC;";
        Object[] pushRecordParams = new Object[]{userIdx, userIdx};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetPushListRes(
                        rs.getInt("id"),
                        null,
                        null,
                        rs.getInt("owner_userIdx"),
                        rs.getInt("join_userIdx"),
                        rs.getInt("matchIdx"),
                        rs.getString("game_time"),
                        rs.getString("network_type"),
                        rs.getString("onlydate"),
                        rs.getString("status")
                ), pushRecordParams);
    }

    public GetUserProfileImgRes getUserProfileImg(int userIdx){
        String query = "SELECT id, profile_imgurl from user where id = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetUserProfileImgRes(
                        rs.getInt("id"),
                        rs.getString("profile_imgurl"))
        , userIdx);
    }


}
