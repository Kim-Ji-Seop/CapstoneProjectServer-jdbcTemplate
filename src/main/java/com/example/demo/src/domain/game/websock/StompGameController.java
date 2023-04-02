package com.example.demo.src.domain.game.websock;

import com.example.demo.src.domain.game.dto.AdminSendScoreDTO;
import com.example.demo.src.domain.game.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompGameController {

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달

    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
    @MessageMapping(value = "/game/enter")
    public void enter(ChatMessageDTO message){
        message.setMessage(message.getWriter() + " 님이 매칭방에 참여하였습니다.");
        template.convertAndSend("/sub/game/room/" + message.getMatchIdx(), message);
    }

    @MessageMapping(value = "/game/message")
    public void message(ChatMessageDTO message){ // 점수 DTO로 수정해야함
        System.out.println(message.getMatchIdx() + ": " + message.getWriter() + " -> " + message.getMessage());
        template.convertAndSend("/sub/game/room/" + message.getMatchIdx(), message);
    }
    @MessageMapping(value = "/game/start-game")
    public void messageToClient(AdminSendScoreDTO message){
        System.out.println(message.getMatchIdx() + ": " + message.getWriter() + " -> " + message.getScore() + " score ");
        template.convertAndSend("/sub/game/room/" + message.getMatchIdx(), new AdminSendScoreDTO(message.getMatchIdx(),message.getWriter(),message.getScore()));
    }
}
