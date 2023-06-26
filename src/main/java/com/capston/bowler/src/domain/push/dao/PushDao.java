package com.capston.bowler.src.domain.push.dao;

import com.capston.bowler.src.domain.history.dao.HistoryDao;
import com.capston.bowler.src.domain.push.dto.JoinAcceptOrNotReq;
import com.capston.bowler.src.domain.push.dto.MatchCancelReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class PushDao {

    private JdbcTemplate jdbcTemplate;
    private HistoryDao historyDao;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int joinPush(int targetUserIdx, int userIdx, int matchIdx, String push_title, String push_content){
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

    public int ownerAccepted(int userIdx, JoinAcceptOrNotReq joinAcceptOrNotReq, String newStatus) {
        // 1) 매칭 신청에 대해 수락/거절 처리
        String acceptQuery = "UPDATE push SET status = ?\n" +
                "WHERE id = ?";
        int pushIdx = joinAcceptOrNotReq.getPushIdx();

        Object[] pushParams = new Object[]{
                newStatus,
                pushIdx
        };
        this.jdbcTemplate.update(acceptQuery, pushParams);

        return pushIdx;
    }

    public int isOwnerCheck(MatchCancelReq matchCancelReq) {
        String query = "select userIdx from match_room where id = ?";
        return this.jdbcTemplate.queryForObject(query,int.class,matchCancelReq.getMatchIdx());
    }

    public void deleteMatchRoomByOwner(int matchIdx) {
        String query =
                "update history h, match_room mr\n" +
                "    set h.status = 'D',\n" +
                "        mr.status = 'D'\n" +
                "where h.matchIdx = ? and mr.id = ?";

        Object[] params = new Object[]{
                matchIdx,
                matchIdx
        };

        this.jdbcTemplate.update(query,params);
    }

    public void exitMatchRoom(int userIdx, int matchIdx) {
        String query =
                "update history set status = 'D' where matchIdx = ? and userIdx = ?";
        this.jdbcTemplate.update(query,matchIdx,userIdx);
    }
}
