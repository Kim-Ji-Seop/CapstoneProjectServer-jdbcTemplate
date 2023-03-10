package com.example.demo.src.domain.match.dao;

import com.example.demo.config.BaseResponse;
import com.example.demo.src.domain.match.dto.MatchRoomDetailRes;
import com.example.demo.src.domain.match.dto.PostCreateMatchRoomReq;
import com.example.demo.src.domain.match.dto.PostCreateMatchRoomRes;
import com.example.demo.src.domain.user.dto.PostLoginReq;
import com.example.demo.src.domain.user.dto.PostLoginRes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MatchDaoTest { // jUnit4

    @LocalServerPort
    private int port;

    @Autowired
    private MatchDao dao;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void MatchRoom_생성한다() throws Exception{
        // 1) 로그인 후 jwt 생성
        String login_url = "http://localhost:" + port + "/app/users/login/test";
        PostLoginReq postLoginReq = new PostLoginReq("aws5624", "Anwooseong56240@");
        PostLoginRes loginResponse = restTemplate.postForObject(login_url, postLoginReq, PostLoginRes.class);

        // 1-1) 로그인 성공 확인
        int userIdx = loginResponse.getUserIdx();
        String validJwt = loginResponse.getJwt();
        System.out.println("로그인 유저 PK: " + userIdx);
        System.out.println("로그인 유저 JWT: " + validJwt);

        // 2) 매칭방 생성
        // 2-1) 날짜 생성 String, Timestamp 둘 다 사용 가능 - 추후 LocalDateTime
        LocalDateTime now = LocalDateTime.of(2023,03,21,18,0,0);
        Timestamp nowTimestamp = Timestamp.valueOf(now);
        System.out.println(nowTimestamp);

        // 2-2) 매칭방 생성 요청 Request 객체 (날짜 없이 Date를 String으로 보냄)
        PostCreateMatchRoomReq matchRoomReq= new PostCreateMatchRoomReq(
                "찐막테스트",
                "날짜 타입이 이상햇음",
                nowTimestamp, // String, TimeStamp 상관없이 형식에 맞추기만하면 됨.
                2,
                "경상북도 구미",
                "금오볼링장",
                110,
                "OFFLINE",
                10000);

        // 2-3) 매칭방 요청 + jwt Request Body 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-ACCESS-TOKEN", validJwt);
        HttpEntity<PostCreateMatchRoomReq> headerEntity = new HttpEntity(matchRoomReq, headers);

        String createMatchRoom_url = "http://localhost:" + port + "/app/matches/rooms/test";
        URI uri = UriComponentsBuilder.fromUriString(createMatchRoom_url)
                .build()
                .encode()
                .toUri();

        // 2-4) 매칭방 생성 Post 요청 후 Response객체 반환받기
        ResponseEntity<PostCreateMatchRoomRes> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, headerEntity, PostCreateMatchRoomRes.class);

        // 3) 새로운 매칭방 생성 확인
        PostCreateMatchRoomRes newMatchRoomEntity = responseEntity.getBody();
        int newMatchRoomIdx = newMatchRoomEntity.getMatchIdx();
        System.out.println("새로생성된 매칭방 PK: " + newMatchRoomIdx);

        // 3-1) 반환 객체의 필드 -> 새로 생성된 매칭방 PK로 DB에서 해당 매칭방 정보를 조회
        //      조회한 값과 처음에 넣을때 사용했던 요청 값의 필드 를 비교하여 결과 확인
        String matchDetail_url = "http://localhost:" + port + "/app/matches/rooms/" + newMatchRoomIdx + "/test";
        MatchRoomDetailRes checkNewMatchRoom = restTemplate.getForObject(matchDetail_url, MatchRoomDetailRes.class);

        System.out.println("요청 파라미터" + "\t" + "조회 파라미터");
        System.out.println(matchRoomReq.getTitle() + "\t" + checkNewMatchRoom.getTitle());
        System.out.println(matchRoomReq.getContent() + "\t" + checkNewMatchRoom.getContent());
        System.out.println(matchRoomReq.getDate() + "\t" + checkNewMatchRoom.getDate());
        System.out.println(matchRoomReq.getCount() + "\t" + checkNewMatchRoom.getCount());
        System.out.println(matchRoomReq.getLocation() + "\t" + checkNewMatchRoom.getLocation());
        System.out.println(matchRoomReq.getPlace() + "\t" + checkNewMatchRoom.getPlace());
        System.out.println(matchRoomReq.getAverage() + "\t" + checkNewMatchRoom.getTargetScore());
        System.out.println(matchRoomReq.getCost() + "\t" + checkNewMatchRoom.getCost());

        assertThat(matchRoomReq.getTitle()).isEqualTo(checkNewMatchRoom.getTitle());
        assertThat(matchRoomReq.getContent()).isEqualTo(checkNewMatchRoom.getContent());
        //assertThat(matchRoomReq.getDate()).isEqualTo(checkNewMatchRoom.getDate());
        assertThat(matchRoomReq.getCount()).isEqualTo(checkNewMatchRoom.getCount());
        assertThat(matchRoomReq.getLocation()).isEqualTo(checkNewMatchRoom.getLocation());
        assertThat(matchRoomReq.getPlace()).isEqualTo(checkNewMatchRoom.getPlace());
        //assertThat(postCreateMatchRoomReq.getNetworkType()).isEqualTo(checkNewMatchRoom.getNetworkType());
        assertThat(matchRoomReq.getAverage()).isEqualTo(checkNewMatchRoom.getTargetScore());
        assertThat(matchRoomReq.getCost()).isEqualTo(checkNewMatchRoom.getCost());

    }
}