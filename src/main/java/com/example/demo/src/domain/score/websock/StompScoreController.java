package com.example.demo.src.domain.score.websock;

import com.example.demo.src.domain.score.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompScoreController {

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달

    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
    @MessageMapping(value = "/score/enter")
    public void enter(ChatMessageDTO message){
        message.setMessage(message.getWriter() + " 님이 매칭방에 참여하였습니다.");
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    @MessageMapping(value = "/score/message")
    public void message(ChatMessageDTO message){ // 점수 DTO로 수정해야함
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
}