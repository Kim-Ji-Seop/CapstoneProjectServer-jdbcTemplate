package com.example.demo.src.domain.match.dao;

import com.example.demo.src.domain.history.dao.HistoryDao;
import com.example.demo.src.domain.match.dto.*;
import com.example.demo.src.domain.user.dto.GetPushListRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MatchDao {
    private JdbcTemplate jdbcTemplate;
    private HistoryDao historyDao;
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

    // 매칭 전적을 확인하기 위함
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
                "    h.id, h.userIdx, h.matchIdx, h.teamIdx,\n" +
                "    h.settle_type,\n" +
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
                "            GROUP BY h.matchIdx, h.teamIdx\n" +
                "            ORDER BY h.matchidx;";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new MatchRecordsRes(
                        rs.getString("game_time"),
                        rs.getString("nickname"),
                        rs.getString("network_type"),
                        rs.getInt("count"),
                        rs.getInt("userIdx"),
                        rs.getInt("matchIdx"),
                        rs.getInt("teamIdx"),
                        null,
                        rs.getString("settle_type"),
                        rs.getInt("total_score")
                ), userIdx);
    }

    // user simple-info 에서 사용되는 개인 에버리지 기록 확인용


    public PostCreateMatchRoomRes createMatchRoom(PostCreateMatchRoomReq postCreateMatchRoomReq, int userIdx, String matchCode) {
        // 1) 매칭방 생성
        String createMatchRoomQuery = "INSERT INTO match_room(title, content, userIdx, game_time, target_score, location, network_type, `count`, place, cost, match_code)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Object[] createMatchRoomParam = new Object[]{
                postCreateMatchRoomReq.getTitle() ,
                postCreateMatchRoomReq.getContent() ,
                userIdx ,
                postCreateMatchRoomReq.getDate() ,
                postCreateMatchRoomReq.getAverage() ,
                postCreateMatchRoomReq.getLocation() ,
                postCreateMatchRoomReq.getNetworkType() ,
                postCreateMatchRoomReq.getCount() ,
                postCreateMatchRoomReq.getPlace(),
                postCreateMatchRoomReq.getCost(),
                matchCode};

        this.jdbcTemplate.update(createMatchRoomQuery, createMatchRoomParam);

        String lastInsertedQ = "select last_insert_id()";
        int newMatchRoomNum = this.jdbcTemplate.queryForObject(lastInsertedQ, int.class);

        // 2) 반환 완료
       return new PostCreateMatchRoomRes(newMatchRoomNum);
    }

    public int MatchRoomJoinedUserCount(int matchIdx){
        String query = "SELECT COUNT(matchIdx) as currentJoinUserCount \n" +
                "FROM history\n" +
                "WHERE matchIdx = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new Integer(
                        rs.getInt("currentJoinUsercount")
                ), matchIdx);
    }

    public List<GetMatchPlanRes> matchPlanList(int userIdx) {
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
                "    u.nickname, u.profile_imgurl, mr.network_type, mr.count,\n" +
                "    h.id, h.userIdx, h.matchIdx, h.teamIdx,\n" +
                "    h.settle_type,\n" +
                "    mr.place, mr.status,\n" +
                "    IF(h.teamIdx = (SELECT teamIdx FROM history WHERE matchIdx=h.matchIdx and userIdx=?), 'HOME', 'AWAY') as homeOrAway\n" +
                "FROM history AS h\n" +
                "    LEFT JOIN match_room mr on h.matchIdx = mr.id\n" +
                "    LEFT JOIN user u on u.id = h.userIdx\n" +
                "WHERE h.matchIdx IN(SELECT h.matchIdx FROM history AS h WHERE h.userIdx = ?)\n" +
                "GROUP BY h.matchIdx, h.teamIdx\n" +
                "ORDER BY h.matchidx;";

        Object[] planParam = new Object[]{
                userIdx, userIdx
        };

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetMatchPlanRes(
                        rs.getString("game_time"),
                        rs.getString("network_type"),
                        rs.getString("nickname"),
                        rs.getString("profile_imgurl") == null ? " ": rs.getString("profile_imgurl"),
                        rs.getInt("count"),
                        rs.getInt("id"),
                        rs.getInt("userIdx"),
                        rs.getInt("matchIdx"),
                        rs.getInt("teamIdx"),
                        rs.getString("homeOrAway"),
                        rs.getString("place") == null ? " " : rs.getString("place")
                ), planParam);
    }


    public GetMatchPlanDetailRes matchPlanDetial(int userIdx) {
        String query = "SELECT\n" +
                "    h.teamIdx, h.userIdx, u.nickname, u.profile_imgurl,\n" +
                "    MAX(h.total_score) as highScore,\n" +
                "    ROUND(AVG(h.total_score)) as avgScore,\n" +
                "    COUNT(h.id) as gameCount,\n" +
                "    (SELECT COUNT(settle_type)\n" +
                "     FROM history\n" +
                "     WHERE h.settle_type = 'WIN' AND userIdx = ?) as winCount,\n" +
                "    (SELECT COUNT(settle_type)\n" +
                "     FROM history\n" +
                "     WHERE h.settle_type = 'LOSE' AND userIdx = ?) as loseCount\n" +
                "FROM history h\n" +
                "LEFT JOIN user u ON u.id = h.userIdx\n" +
                "WHERE userIdx = ? AND (settle_type IS NOT NULL AND total_score IS NOT NULL)";

        Object [] param = new Object[] {userIdx, userIdx, userIdx};

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetMatchPlanDetailRes(
                        rs.getInt("teamIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickname"),
                        rs.getString("profile_imgurl") == null? "":rs.getString("profile_imgurl"),
                        rs.getInt("highScore"),
                        rs.getInt("avgScore"),
                        rs.getInt("gameCount"),
                        rs.getInt("winCount"),
                        rs.getInt("loseCount"),
                        null
                ), param);
    }

    public List<MatchCandidate> matchCandidates (int matchIdx){
        String query = "SELECT userIdx, teamIdx FROM history WHERE matchIdx = ?";
        return this.jdbcTemplate.query(query,
                (rs,rowNum) -> new MatchCandidate(
                        rs.getInt("userIdx"),
                        rs.getInt("teamIdx"))
        , matchIdx);
    }

    public int getTeamIdx(int matchIdx, int userIdx){
        String query = "SELECT teamIdx FROM history " +
                "WHERE matchIdx = " + matchIdx + " and userIdx = " + userIdx;
        return this.jdbcTemplate.queryForObject(query, Integer.class);
    }

    public String getMatchCode(int matchIdx) {
        String query = "SELECT match_code FROM match_room\n" +
                "WHERE id = " + matchIdx;
        return this.jdbcTemplate.queryForObject(query, String.class);
    }
    public String getGameTime(int matchIdx){
        String query = "SELECT \n" +
                "    CASE\n" +
                "        WHEN\n" +
                "            instr(date_format(game_time, '%Y-%m-%d %p %h:%i'), 'PM') > 0\n" +
                "        THEN\n" +
                "            replace(date_format(game_time, '%Y-%m-%d %p %h:%i'), 'PM', '오후')\n" +
                "        ELSE\n" +
                "            replace(date_format(game_time, '%Y-%m-%d %p %h:%i'), 'AM', '오전')\n" +
                "    END as game_time\n" +
                "FROM match_room\n" +
                "WHERE id = "+ matchIdx + "\n";
        return this.jdbcTemplate.queryForObject(query, String.class);

    }
}
