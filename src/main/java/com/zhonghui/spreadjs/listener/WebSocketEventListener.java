package com.zhonghui.spreadjs.listener;

import com.zhonghui.spreadjs.interceptor.CustomSubProtocolWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 * @author xwb
 */
@Component
public class WebSocketEventListener {

    @Autowired
    WebSocketHandler webSocketHandler;

    @EventListener
    public void onSessionConnectedEvent(SessionConnectedEvent event) {
        CustomSubProtocolWebSocketHandler customSubProtocolWebSocketHandler=(CustomSubProtocolWebSocketHandler)webSocketHandler;
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        System.out.println("onSessionConnectedListener:"+headerAccessor.getSessionId());
    }

    @EventListener
    public void onSessionSubscribeEvent(SessionSubscribeEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("onSessionSubscribeEvent:"+headerAccessor.getSessionId());
    }
}
