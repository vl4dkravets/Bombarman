package org.bombermen.configurations;

import org.bombermen.message.EventHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
//    @Autowired
//    private EventHandler eventHandler;
//
//    @Bean
//    @Scope("prototype")
//    public EventHandler personSingleton() {
//        return new EventHandler();
//    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new EventHandler(), "/events/connect")
                .setAllowedOrigins("*")
        //        .withSockJS()
        ;
    }

}
