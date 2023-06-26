package com.capston.bowler.src.domain.match.dao;

import com.capston.bowler.src.domain.history.dao.HistoryDao;
import com.capston.bowler.src.domain.match.dto.*;
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

    public PossibleMatchesRes onlineCountMatches() {
        String query = "select count(*) as cnt " +
                "from match_room " +
                "where status='A' AND network_type = 'ONLINE'";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new PossibleMatchesRes(rs.getInt("cnt")));
    }

    public PossibleMatchesRes localCountMatches(int localIdx){
        String query = "SELECT\n" +
                "    COUNT(*) as cnt\n" +
                "FROM match_room\n" +
                "WHERE status = 'A' AND network_type ='OFFLINE' AND locationIdx = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new PossibleMatchesRes(rs.getInt("cnt")),
                localIdx);
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
                "where network_type = ? and status = 'A'";
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
                "where network_type = ? and status = 'A'";
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
    public List<Integer> getMatchRecord(int userIdx){
        String query = "SELECT\n" +
                "    matchIdx\n" +
                "FROM history\n" +
                "WHERE userIdx = ? AND total_score IS NOT NULL\n" +
                "ORDER BY updated DESC, matchIdx";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new Integer(
                        rs.getInt("matchIdx")
                ), userIdx);
    }

    public List<UserHistoryInfo> getAllUserInfoByMatchIdx(int matchIdx){
        String query = "SELECT\n" +
                "    userIdx, matchIdx, teamIdx, settle_type, total_score\n" +
                "FROM history\n" +
                "WHERE matchIdx = ? AND total_score IS NOT NULL \n" +
                "ORDER BY updated DESC, matchIdx, teamIdx";

        return this.jdbcTemplate.query(query,
                ((rs, rowNum) -> new UserHistoryInfo(
                        rs.getInt("userIdx"),
                        rs.getInt("matchIdx"),
                        rs.getInt("teamIdx"),
                        rs.getString("settle_type"),
                        rs.getInt("total_score"))
                ), matchIdx);
    }

    public List<UserHistoryInfo> getAllUserInfoByMatchIdxN(int matchIdx){
        String query = "SELECT\n" +
                "    userIdx, matchIdx, teamIdx, settle_type, " +
                "    SUM(total_score) as total_score\n" +
                "FROM history\n" +
                "WHERE matchIdx = ? AND total_score IS NOT NULL\n" +
                "GROUP BY matchIdx, teamIdx\n" +
                "ORDER BY updated DESC, matchIdx, teamIdx";

        return this.jdbcTemplate.query(query,
                ((rs, rowNum) -> new UserHistoryInfo(
                        rs.getInt("userIdx"),
                        rs.getInt("matchIdx"),
                        rs.getInt("teamIdx"),
                        rs.getString("settle_type"),
                        rs.getInt("total_score"))
                ), matchIdx);
    }

    public int getMatchRoomPeopleLimit(int matchIdx){
        String query = "SELECT count FROM match_room WHERE id = ?";

        return this.jdbcTemplate.queryForObject(query,
                ((rs, rowNum) -> new Integer(
                        rs.getInt("count"))
                ), matchIdx);
    }

    public String getMatchRoomNetworkTypeById(int matchIdx){
        String query = "SELECT network_type FROM match_room WHERE id = ?";
        return this.jdbcTemplate.queryForObject(query,
                ((rs, rowNum) -> new String(
                        rs.getString("network_type"))
                ), matchIdx);
    }


    // 매칭방 생성
    public PostCreateMatchRoomRes createMatchRoom(PostCreateMatchRoomReq postCreateMatchRoomReq, int userIdx, String matchCode, int localIdx) {
        // 1) 매칭방 생성
        String createMatchRoomQuery = "INSERT INTO match_room(title, content, userIdx, game_time, target_score, location, network_type, `count`, place, cost, match_code, locationIdx)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
                matchCode,
                localIdx == 0 ? null : localIdx};

        this.jdbcTemplate.update(createMatchRoomQuery, createMatchRoomParam);

        String lastInsertedQ = "select last_insert_id()";
        int newMatchRoomNum = this.jdbcTemplate.queryForObject(lastInsertedQ, int.class);

        // 2) 반환 완료
        return new PostCreateMatchRoomRes(newMatchRoomNum);
    }

    // 현재 매칭방에 참여하고 있는 인원 수
    public int matchRoomJoinedUserCount(int matchIdx){
        String query = "SELECT COUNT(matchIdx) as currentJoinUserCount \n" +
                "FROM history\n" +
                "WHERE matchIdx = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new Integer(
                        rs.getInt("currentJoinUsercount")
                ), matchIdx);
    }


    public List<GetMatchPlanRes> matchPlanList(int userIdx) {
        String query =
                "SELECT (SELECT\n" +
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
                "WHERE h.matchIdx IN(SELECT h.matchIdx FROM history AS h WHERE h.userIdx = ? AND h.status = 'A') AND mr.status = 'A' AND h.status = 'A'\n" +
                "GROUP BY h.matchIdx, h.teamIdx\n" +
                "ORDER BY game_time DESC;";

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


    public GetMatchPlanDetailRes matchPlanDetail(MatchCandidate joinUser) {
        String query = "SELECT\n" +
                "    MAX(h.total_score) as highScore,\n" +
                "    ROUND(AVG(h.total_score)) as avgScore,\n" +
                "    COUNT(h.id) as gameCount,\n" +
                "    (SELECT COUNT(settle_type)\n" +
                "     FROM history\n" +
                "     WHERE settle_type = 'WIN' AND userIdx = ?) as winCount,\n" +
                "    (SELECT COUNT(settle_type)\n" +
                "     FROM history\n" +
                "     WHERE settle_type = 'LOSE' AND userIdx = ?) as loseCount\n" +
                "FROM history h\n" +
                "LEFT JOIN user u ON u.id = h.userIdx\n" +
                "WHERE h.userIdx = ? AND (h.settle_type IS NOT NULL AND h.total_score IS NOT NULL) AND h.status <> 'D'";

        int userIdx = joinUser.getUserIdx();
        Object [] param = new Object[] {userIdx, userIdx, userIdx};

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetMatchPlanDetailRes(
                        joinUser.getTeamIdx(),
                        joinUser.getUserIdx(),
                        joinUser.getNickName(),
                        joinUser.getProfile_imgurl() == null? "":joinUser.getProfile_imgurl(),
                        rs.getInt("gameCount") == 0?0:rs.getInt("highScore"),
                        rs.getInt("gameCount") == 0?0:rs.getInt("avgScore"),
                        rs.getInt("gameCount"),
                        rs.getInt("gameCount") == 0?0:rs.getInt("winCount"),
                        rs.getInt("gameCount") == 0?0:rs.getInt("loseCount"),
                        null
                ), param);
    }

    public List<MatchCandidate> matchCandidates(int matchIdx){
        String query ="SELECT\n" +
                "    h.userIdx, h.teamIdx, u.nickname, u.profile_imgurl\n" +
                "FROM history h\n" +
                "LEFT JOIN user u on h.userIdx = u.id\n" +
                "WHERE   h.userIdx IN(SELECT userIdx FROM history WHERE matchIdx = ?) AND h.matchIdx = ?";

        Object[] param = {matchIdx, matchIdx};

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new MatchCandidate(
                        rs.getInt("userIdx"),
                        rs.getInt("teamIdx"),
                        rs.getString("nickname"),
                        rs.getString("profile_imgurl") == null? "":rs.getString("profile_imgurl")
                ), param);
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

    public int getMatchIdxFromHistoryIdx(int historyIdx){
        String query = "SELECT matchIdx FROM history WHERE id = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new Integer(
                        rs.getInt("matchIdx")
                ), historyIdx);
    }

    public void matchGameOver(int matchIdx){
        String query = "UPDATE match_room " +
                "SET status = 'E' " +
                "WHERE id = ?";
        this.jdbcTemplate.update(query, matchIdx);
    }


    public List<String> getLocalCities(String local) {
        String query = "SELECT l.city FROM location l\n" +
                "WHERE l.local = ?";
        return this.jdbcTemplate.query(query,
                (rs,rowNum) ->
                        new String(rs.getString("city"))
                , local);
    }

    // 매칭방 지역 단위, 도시 단위로 조회
    public List<ByNetworkRes> getmatchRoomsOfflineByLocalCity(String network, int locationIdx) {
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
                "WHERE network_type = ? and status = 'A' AND  locationIdx = ?";

        Object [] param = {network, locationIdx};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ByNetworkRes(
                        rs.getString("game_time"),
                        rs.getInt("target_score"),
                        rs.getString("place"),
                        rs.getInt("count"),
                        rs.getInt("id")
                ),param);
    }

    public int getLocationIdx(String localName, String cityName){
        String query = "SELECT id FROM location WHERE `local` = ? AND city = ?";
        Object [] param = {localName, cityName};

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new Integer (rs.getInt("id")), param);
    }

    // 매칭방 지역 단위로 조회
    public List<ByNetworkRes> getmatchRoomsOfflineByLocal(String network, String localName) {
        String query = "select\n" +
                "    mr.count,\n" +
                "    case\n" +
                "        when\n" +
                "            instr(date_format(mr.game_time, '%Y-%m-%d %p %h:%i'), 'PM') > 0\n" +
                "        then\n" +
                "            replace(date_format(mr.game_time, '%Y-%m-%d %p %h:%i'), 'PM', '오후')\n" +
                "        else\n" +
                "            replace(date_format(mr.game_time, '%Y-%m-%d %p %h:%i'), 'AM', '오전')\n" +
                "    end as game_time,\n" +
                "    mr.place,\n" +
                "    mr.target_score, mr.id\n" +
                "from match_room mr\n" +
                "INNER JOIN location l on mr.locationIdx = l.id\n" +
                "where mr.network_type = ? and mr.status = 'A' AND  l.local = ?;";

        Object [] param = {network, localName};

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ByNetworkRes(
                        rs.getString("game_time"),
                        rs.getInt("target_score"),
                        rs.getString("place"),
                        rs.getInt("count"),
                        rs.getInt("id")
                ),param);
    }


    // 배치에 사용할 SQL
    public int deactivateMatch(){ // 매치를 상태 D로
        String query = "UPDATE match_room\n" +
                "SET status = 'D'\n" +
                "WHERE game_time < NOW()\n" +
                "  AND TIMESTAMPDIFF(MINUTE, NOW(), game_time) < -10\n" +
                "  AND network_type = 'OFFLINE'\n" +
                "  AND status = 'A'";

        return this.jdbcTemplate.update(query);
    }

    public int deactivateHistory(int matchIdx){ // 매치와 관련된 유저 참여 기록 상태 D로
        String query = "UPDATE history\n" +
                "SET status = 'D'\n" +
                "WHERE matchIdx = ?\n" +
                "  AND (settle_type IS NULL OR total_score IS NULL)\n" +
                "  AND status = 'A'";

        return this.jdbcTemplate.update(query, matchIdx);
    }

    public int unvalidMatchCount(){ // 비활성화 시켜야할 오프라인 매치 수
        String query = "SELECT count(id) as cnt\n" +
                "FROM match_room\n" +
                "WHERE game_time < NOW()\n" +
                "  AND TIMESTAMPDIFF(MINUTE, NOW(), game_time) < -10\n" +
                "  AND network_type = 'OFFLINE'\n" +
                "  AND status = 'A';";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getInt("cnt"));
    }

    public List<Integer> unvalidMatchIdxList(){ // 비활성화 시켜야할 오프라인 매치 Id 리스트
        String query = "SELECT id\n" +
                "FROM match_room\n" +
                "WHERE game_time < NOW()\n" +
                "  AND TIMESTAMPDIFF(MINUTE, NOW(), game_time) < -10\n" +
                "  AND network_type = 'OFFLINE'\n" +
                "  AND status = 'A';";

        return this.jdbcTemplate.query(query,
                (rs,rowNum) -> rs.getInt("id"));
    }
}
