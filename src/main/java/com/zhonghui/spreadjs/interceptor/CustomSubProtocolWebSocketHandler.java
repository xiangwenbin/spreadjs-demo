package com.zhonghui.spreadjs.interceptor;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

/**
 * @author xwb
 */
public class CustomSubProtocolWebSocketHandler extends SubProtocolWebSocketHandler {


    public CustomSubProtocolWebSocketHandler(MessageChannel clientInboundChannel, SubscribableChannel clientOutboundChannel) {
        super(clientInboundChannel, clientOutboundChannel);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("CustomSubProtocolWebSocketHandler:"+session.getId());
        super.afterConnectionEstablished(session);
    }


}
