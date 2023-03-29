package com.insurance.kakao.insurace.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
	@Bean
	public Docket api(TypeResolver typeResolver){
		return new Docket(DocumentationType.OAS_30)
				.additionalModels(
						typeResolver.resolve(Void.class)
				)
				.useDefaultResponseMessages(true)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.insurance.kakao"))
				.paths(PathSelectors.any())
				.build();
	}

	public ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("카카오페이 손해보험 사전과제")
				.description("계약관리시스템")
				.version("0.1")
				.build();
	}
}
