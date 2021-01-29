package com.zhonghui.spreadjs;

import com.zhonghui.spreadjs.interceptor.CustomStompSubProtocolHandler;
import com.zhonghui.spreadjs.interceptor.InChannelInterceptor;
import com.zhonghui.spreadjs.interceptor.OutChannelInterceptor;
import com.zhonghui.spreadjs.interceptor.WebSocketHandshakeHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.messaging.SubProtocolHandler;

/**
 * @author xwb
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration  implements WebSocketMessageBrokerConfigurer {

    @Autowired
    WebSocketHandshakeHandlerInterceptor webSocketHandshakeHandlerInterceptor;

    @Autowired
    InChannelInterceptor inChannelInterceptor;

    @Autowired
    OutChannelInterceptor outChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //websocket 连接地址
        registry.addEndpoint("/ws/stomp").addInterceptors(webSocketHandshakeHandlerInterceptor).setAllowedOrigins("*").withSockJS().setStreamBytesLimit(Integer.MAX_VALUE).setHttpMessageCacheSize(Integer.MAX_VALUE);


    }


    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(Integer.MAX_VALUE);
        registry.setSendBufferSizeLimit(Integer.MAX_VALUE);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration channelRegistration) {
        channelRegistration.interceptors(inChannelInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration channelRegistration) {
        channelRegistration.interceptors(outChannelInterceptor);
    }



    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        //客户端可订阅总类
        config.enableSimpleBroker("/doc", "/user").setTaskScheduler(new DefaultManagedTaskScheduler()).setHeartbeatValue(new long[]{10000L, 10000L});
    }


}