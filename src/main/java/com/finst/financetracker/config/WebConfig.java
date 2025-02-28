package com.finst.financetracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public final ApiKeyAuthorizationInterceptor apiKeyAuthorizationInterceptor;

    public WebConfig(ApiKeyAuthorizationInterceptor apiKeyAuthorizationInterceptor) {
        this.apiKeyAuthorizationInterceptor = apiKeyAuthorizationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiKeyAuthorizationInterceptor)
        .addPathPatterns("/v1/**")
                .excludePathPatterns(List.of("/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**","/swagger-ui/index.html"));
    }
}
