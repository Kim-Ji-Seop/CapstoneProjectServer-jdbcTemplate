package com.capston.bowler.src.domain.game.websock;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@Configuration
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatPreHandler chatPreHandler;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { // 클라이언트가 웹소켓 서버에 연결할 수 있는 엔드포인트를 설정합니다. "/stomp/game" 경로로 접근할 수 있으며, "https://www.seop.site"에서 오는 요청만 허용됩니다.
        registry.addEndpoint("/stomp/game")
                .setAllowedOrigins("https://www.seop.site")
                .withSockJS();
    }

    /*어플리케이션 내부에서 사용할 path를 지정할 수 있음*/
    // 메시지 브로커에 관한 설정을 합니다. 클라이언트가 메시지를 보낼 때는 "/pub" 경로를 사용하며, 서버가 클라이언트에게 메시지를 보낼 때는 "/sub" 경로를 사용합니다.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // pub 경로로 수신되는 STOMP메세지는 @Controller 객체의 @MessageMapping 메서드로 라우팅 됨
        // SipmleAnnotationMethod, 메시지를 발행하는 요청 url => 클라이언트가 메시지를 보낼 때 (From Client)
        registry.setApplicationDestinationPrefixes("/pub");
        // SimpleBroker,  클라이언트에게 메시지를 보낼 때 (To Client)
        registry.enableSimpleBroker("/sub");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) { // 클라이언트로부터 들어오는 메시지를 처리할 인터셉터를 설정합니다. 이 경우에는 ChatPreHandler가 이 역할을 담당합니다.
        registration.interceptors(chatPreHandler);
    }
}
