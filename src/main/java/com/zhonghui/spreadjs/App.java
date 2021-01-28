package com.zhonghui.spreadjs;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 *
 * @author xwb
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.zhonghui.core","com.zhonghui.spreadjs"})
@Configuration
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
