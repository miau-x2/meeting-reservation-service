package com.example.meeting.reservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration(proxyBeanMethods = false)
public class PageableConfig {

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer customizer() {
        return resolver -> {
            resolver.setOneIndexedParameters(true);
            resolver.setMaxPageSize(20);
            resolver.setFallbackPageable(PageRequest.of(0, 10));
        };
    }
}
