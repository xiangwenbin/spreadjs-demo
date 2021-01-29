package com.zhonghui.spreadjs.interceptor;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.BufferingStompDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;

/**
 * @author xwb
 */
public class CustomStompSubProtocolHandler extends StompSubProtocolHandler {

    @Override
    public void afterSessionStarted(WebSocketSession session, MessageChannel outputChannel) {
        session.setTextMessageSizeLimit(1024*64);
        super.afterSessionStarted(session,outputChannel);
    }
}
