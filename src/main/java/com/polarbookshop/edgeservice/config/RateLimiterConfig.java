package com.polarbookshop.edgeservice.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver keyResolver() {
        // 요청에 대한 사용률 제한은 상수 키를 사용해 적용
        return exchange -> exchange.getPrincipal()
                .map(Principal::getName)
                .defaultIfEmpty("anonymous");   // 요청이 인증되지 않으면 사용율 제한을 적용하기 위한 기본키 값으로 anonymous를 사용
    }
}
