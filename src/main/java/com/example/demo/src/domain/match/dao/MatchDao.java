package com.example.demo.src.domain.match.dao;

import com.example.demo.src.domain.match.dto.ByNetworkRes;
import com.example.demo.src.domain.match.dto.MatchRoomDetailRes;
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
                "    target_score\n" +
                "from match_room\n" +
                "where network_type = ?";
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ByNetworkRes(
                        rs.getString("game_time"),
                        rs.getInt("target_score")
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
                "    target_score\n" +
                "from match_room\n" +
                "where network_type = ?";
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ByNetworkRes(
                        rs.getString("game_time"),
                        rs.getInt("target_score"),
                        rs.getString("place"),
                        rs.getInt("count")
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
                "     ,mr.location,mr.place\n" +
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
                        rs.getString("place")
                ),matchIdx);
    }
}
