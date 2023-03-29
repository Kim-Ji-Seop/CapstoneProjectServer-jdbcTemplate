package com.example.demo.src.domain.game.websock;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Log4j
public class ChatPreHandler extends ChannelInterceptorAdapter {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // 클라이언트(외부)에서 받은 메세지를 Stomp 프로토콜 형태의 메세지로 가공하는 작업
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        // 1번 방법: command null일 경우는 무시
        if (command != null) {
            switch (command) {
                case CONNECT:
                    System.out.println("유저접속...");
                    break;
                case DISCONNECT:
                    System.out.println("유저퇴장...");
                    break;
                default:
                    System.out.println("다른커맨드...");
                    break;
            }
        }
        // 2번 방법: SimpMessageType을 사용
        /*SimpMessageType messageType = accessor.getMessageType();
        switch (messageType) {
    	...
            case HEARTBEAT:
                log.info("핫빗 날라옴...");
                break;
        ...
        }*/


        return message;
    }
}
