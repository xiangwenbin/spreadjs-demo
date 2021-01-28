package com.zhonghui.spreadjs.interceptor;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * @author xwb
 */
@Component
public class MyChannelInterceptor implements ChannelInterceptor {

	/**
	 * client send to server
	 * @param message
	 * @param channel
	 * @return
	 */
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		StompCommand command = accessor.getCommand();
		System.out.println("MyChannelInterceptor preSend messageType: "+command.getMessageType());
		return message;
	}




}
