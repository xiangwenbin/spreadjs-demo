package com.zhonghui.spreadjs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author xwb
 *
 */
@Configuration
@EnableSwagger2
@Profile({"dev","test"})
public class SwaggerConfiguration  {

	@Bean
	public Docket docket1() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
		          .select()                              
		          .apis(RequestHandlerSelectors.basePackage("com.zhonghui.spreadjs.controller"))
		          .paths(PathSelectors.any())                          
		          .build();  
	}
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
                .title("API")
                .description("项目描述 接口api文档")
                .termsOfServiceUrl("")
                .build();
	}
}
