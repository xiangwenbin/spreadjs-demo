package com.zhonghui.spreadjs;

import com.zhonghui.spreadjs.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.messaging.SubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

import java.util.List;

/**
 * @author xwb
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration extends WebSocketMessageBrokerConfigurationSupport implements WebSocketMessageBrokerConfigurer {

    @Autowired
    WebSocketHandshakeHandlerInterceptor webSocketHandshakeHandlerInterceptor;

    @Autowired
    InChannelInterceptor inChannelInterceptor;

    @Autowired
    OutChannelInterceptor outChannelInterceptor;

    /**
     * 256k
     */
    public static final int MESSAGE_SIZE=262144;




    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //websocket 连接地址
        registry.addEndpoint("/ws/stomp").addInterceptors(webSocketHandshakeHandlerInterceptor).setAllowedOrigins("*").withSockJS();

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        //客户端可订阅总类
        config.enableSimpleBroker("/doc", "/user").setTaskScheduler(new DefaultManagedTaskScheduler()).setHeartbeatValue(new long[]{10000L, 10000L});
    }


    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(MESSAGE_SIZE);
//        registry.setSendBufferSizeLimit(MESSAGE_SIZE);
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
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        super.addReturnValueHandlers(returnValueHandlers);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        return super.configureMessageConverters(messageConverters);
    }


    @Override
    @Bean
    public WebSocketHandler subProtocolWebSocketHandler() {
        return new CustomSubProtocolWebSocketHandler(this.clientInboundChannel(), this.clientOutboundChannel());
    }


}