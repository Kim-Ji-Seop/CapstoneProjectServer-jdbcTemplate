package com.example.demo.src.domain.game.websock;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@Configuration
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/game")
                .setAllowedOrigins("wss://www.seop.stie:9000")
                .withSockJS();
    }

    /*어플리케이션 내부에서 사용할 path를 지정할 수 있음*/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // pub 경로로 수신되는 STOMP메세지는 @Controller 객체의 @MessageMapping 메서드로 라우팅 됨
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");
    }
}
