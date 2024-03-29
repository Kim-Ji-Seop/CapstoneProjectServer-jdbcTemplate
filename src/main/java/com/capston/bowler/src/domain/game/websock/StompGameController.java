package com.capston.bowler.src.domain.game.websock;

import com.capston.bowler.src.domain.game.dto.AdminSendScoreDTO;
import com.capston.bowler.src.domain.game.dto.ScoreSendMessageDTO;
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
    public void enter(ScoreSendMessageDTO message){
        message.setMessage(message.getWriter() + " 님이 매칭방에 참여하였습니다.");
        template.convertAndSend("/sub/game/room/" + message.getMatchIdx(), message);
    }

    @MessageMapping(value = "/game/message")
    public void message(ScoreSendMessageDTO message){ // 점수 DTO로 수정해야함
        System.out.println(message.getMatchIdx() + ": " + message.getWriter() + " -> " + message.getMessage());
        template.convertAndSend("/sub/game/room/" + message.getMatchIdx(), message);
    }
    @MessageMapping(value = "/game/start-game")
    public void messageToClient(AdminSendScoreDTO message){
        System.out.println(message.getMatchIdx() + ": " + message.getWriter() + " -> " + message.getScore() + " score ");
        template.convertAndSend("/sub/game/room/" + message.getMatchIdx(), new AdminSendScoreDTO(message.getPlayerNum(), message.getMatchIdx(),message.getWriter(),message.getScore()));
    }
}
