package com.zhonghui.spreadjs.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xwb
 */
@Component
public class WebSocketHandshakeHandlerInterceptor implements HandshakeInterceptor {
	@Override
	public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
		System.out.println("uri:"+serverHttpRequest.getURI());

		String query=serverHttpRequest.getURI().getQuery();
		String[] params={};
		if(query!=null){
			params=query.split("&");
		}
		Map<String,String> paramMap=new HashMap();
		for(int i=0;i<params.length;i++){
			String[] keyValue=params[i].split("=");
			paramMap.put(keyValue[0],keyValue[1]);
		}

		List<String> cookieList=serverHttpRequest.getHeaders().get("Cookie");
		Map<String,String> cookieMap=new HashMap();
		for(int i=0;cookieList!=null&&i<cookieList.size();i++){
			String[] cookies=cookieList.get(i).split(";");
			for(int j=0;j<cookies.length;j++){
				String[] keyValues=cookies[j].split("=");
				cookieMap.put(keyValues[0],keyValues[1]);
			}
		}

		System.out.println("Authorization:"+serverHttpRequest.getHeaders().get("Authorization"));
		if(cookieMap.get("token")!=null){
			System.out.println(cookieMap.get("token"));
			return true;
		}else if(serverHttpRequest.getHeaders().get("Authorization")!=null){
			return true;
		}else{
			return true;
		}




	}

	@Override
	public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
		System.out.println("afterHandshake");
	}
//	@Override
//	public boolean doHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws HandshakeFailureException {
//		System.out.println(serverHttpRequest.getURI());
//		String query=serverHttpRequest.getURI().getQuery();
//		String[] params={};
//		if(query!=null){
//			params=query.split("&");
//		}
//		Map<String,String> paramMap=new HashedMap();
//		for(int i=0;i<params.length;i++){
//			String[] keyValue=params[i].split("=");
//			paramMap.put(keyValue[0],keyValue[1]);
//		}
//		if("xwb".equals(serverHttpRequest.getHeaders().get("Authorization"))||"xwb".equals(paramMap.get("token"))){
//			return true;
//		}else{
//			return false;
//		}
//	}

}
