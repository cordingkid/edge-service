package com.polarbookshop.edgeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http){
        return http.authorizeExchange(exchange -> exchange.anyExchange().authenticated())       // 모든 요청에 대해 인증 해야함
                .formLogin(Customizer.withDefaults())                                           // 로그인 양식을 사용한 사용자 인증 활성화
                .build();
    }
}
