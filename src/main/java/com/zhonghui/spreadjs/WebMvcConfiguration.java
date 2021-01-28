/**
 */
package com.zhonghui.spreadjs;
import com.zhonghui.spreadjs.interceptor.UserSecurityInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author xwb
 * 添加servlet filter bean
 */

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {


    @Bean
    public UserSecurityInterceptor userSecurityInterceptor() {
        return new UserSecurityInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration demoRegistry=registry.addInterceptor(userSecurityInterceptor());
        demoRegistry.excludePathPatterns("/dist/**");
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);

    }

}

