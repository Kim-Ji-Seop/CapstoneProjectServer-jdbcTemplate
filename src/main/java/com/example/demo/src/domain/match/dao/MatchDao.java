package com.example.demo.src.domain.match.dao;

import com.example.demo.src.domain.match.dto.*;
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

    public List<ByNetworkRes> getMatchRoomsOnline(String network) {
        network = "Online";
        String query = "select\n" +
                "    case\n" +
                "        when\n" +
                "            instr(date_format(game_time, '%Y-%m-%d %p %h:%i'), 'PM') > 0\n" +
                "        then\n" +
                "            replace(date_format(game_time, '%Y-%m-%d %p %h:%i'), 'PM', '오후')\n" +
                "        else\n" +
                "            replace(date_format(game_time, '%Y-%m-%d %p %h:%i'), 'AM', '오전')\n" +
                "    end as game_time,\n" +
                "    target_score,id\n" +
                "from match_room\n" +
                "where network_type = ?";
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ByNetworkRes(
                        rs.getString("game_time"),
                        rs.getInt("target_score"),
                        rs.getInt("id")
                ),network);
    }

    public List<ByNetworkRes> getMatchRoomsOffline(String network) {
        network = "Offline";
        String query = "select\n" +
                "    `count`,\n" +
                "    case\n" +
                "        when\n" +
                "            instr(date_format(game_time, '%Y-%m-%d %p %h:%i'), 'PM') > 0\n" +
                "        then\n" +
                "            replace(date_format(game_time, '%Y-%m-%d %p %h:%i'), 'PM', '오후')\n" +
                "        else\n" +
                "            replace(date_format(game_time, '%Y-%m-%d %p %h:%i'), 'AM', '오전')\n" +
                "    end as game_time,\n" +
                "    place,\n" +
                "    target_score,id\n" +
                "from match_room\n" +
                "where network_type = ?";
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ByNetworkRes(
                        rs.getString("game_time"),
                        rs.getInt("target_score"),
                        rs.getString("place"),
                        rs.getInt("count"),
                        rs.getInt("id")
                ),network);
    }

    public MatchRoomDetailRes matchroomDetail(int matchIdx) {
        String query = "select\n" +
                "    case\n" +
                "        when\n" +
                "            instr(date_format(mr.game_time, '%Y-%m-%d %p %h:%i'), 'PM') > 0\n" +
                "        then\n" +
                "            replace(date_format(mr.game_time, '%Y-%m-%d %p %h:%i'), 'PM', '오후')\n" +
                "        else\n" +
                "            replace(date_format(mr.game_time, '%Y-%m-%d %p %h:%i'), 'AM', '오전')\n" +
                "    end as game_time\n" +
                "     ,u.nickname,mr.title, mr.content,mr.`count`,mr.target_score,mr.cost\n" +
                "     ,mr.location,mr.place, u.id\n" +
                "from match_room mr\n" +
                "join user u on u.id = mr.userIdx\n" +
                "where mr.id = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new MatchRoomDetailRes(
                        rs.getString("game_time"),
                        rs.getString("nickname"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("count"),
                        rs.getInt("target_score"),
                        rs.getInt("cost"),
                        rs.getString("location"),
                        rs.getString("place"),
                        rs.getInt("id")
                ),matchIdx);
    }

    public List<MatchRecordsRes> getMatchRecord(int userIdx){
        String query = "SELECT (SELECT\n" +
                "    CASE\n" +
                "        WHEN\n" +
                "            instr(date_format(mr.game_time, '%Y-%m-%d %p %h:%i'), 'PM') > 0\n" +
                "        THEN\n" +
                "            replace(date_format(mr.game_time, '%Y-%m-%d %p %h:%i'), 'PM', '오후')\n" +
                "        ELSE\n" +
                "            replace(date_format(mr.game_time, '%Y-%m-%d %p %h:%i'), 'AM', '오전')\n" +
                "    END\n" +
                "    FROM match_room AS mr WHERE h.matchIdx = mr.id) AS game_time,\n" +
                "    u.nickname, mr.network_type, mr.count,\n" +
                "    h.id, h.userIdx, h.matchIdx, h.teamIdx, h.settle_type,\n" +
                "    IF (mr.count = 2, h.total_score, SUM(h.total_score)) AS total_score,\n" +
                "    h.created, h.updated, h.status\n" +
                "FROM history AS h\n" +
                "    LEFT JOIN match_room mr on h.matchIdx = mr.id\n" +
                "    LEFT JOIN user u on u.id = h.userIdx\n" +
                "           WHERE h.matchIdx IN(\n" +
                "                SELECT h.matchIdx FROM history AS h\n" +
                "                LEFT JOIN\n" +
                "                    user AS u ON h.userIdx = u.id\n" +
                "                           WHERE u.id = ?)\n" +
                "            GROUP BY h.matchIdx, h.teamIdx;";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new MatchRecordsRes(
                        rs.getString("game_time"),
                        rs.getString("nickname"),
                        rs.getString("network_type"),
                        rs.getInt("count"),
                        rs.getInt("teamIdx"),
                        rs.getString("settle_type"),
                        rs.getInt("total_score")
                ), userIdx);
    }

    public PostCreateMatchRoomRes createMatchRoom(PostCreateMatchRoomReq postCreateMatchRoomReq, int userIdx) {
        String query = "INSERT INTO match_room(title, content, userIdx, game_time, target_score, location, network_type, `count`, place, cost)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Object[] queryParam = new Object[]{
                postCreateMatchRoomReq.getTitle() ,
                postCreateMatchRoomReq.getContent() ,
                userIdx ,
                postCreateMatchRoomReq.getDate() ,
                postCreateMatchRoomReq.getAverage() ,
                postCreateMatchRoomReq.getLocation() ,
                postCreateMatchRoomReq.getNetworkType() ,
                postCreateMatchRoomReq.getCount() ,
                postCreateMatchRoomReq.getPlace(),
                postCreateMatchRoomReq.getCost()};

        this.jdbcTemplate.update(query, queryParam);

        String lastInsertedQ = "select last_insert_id()";

        int newMatchRoomNum = this.jdbcTemplate.queryForObject(lastInsertedQ, int.class);
        return new PostCreateMatchRoomRes(newMatchRoomNum);
    }
}
