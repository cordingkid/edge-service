package com.polarbookshop.edgeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

// @EnableWebFluxSecurity를 사용하면 자동으로 다른 스프링 시큐리티 기능들을 사용해서 "/" 를 요청해도 리다이렉션 됫던것
// @Configuration(proxyBeanMethods = false) 이 어노테이션을 사용해서 해당 클래스에서만 정의한 기능만 빈으로 등록되게 해서 해결
//@EnableWebFluxSecurity 이거 주석 처리시 permitAll() 무시되는 문제 해결
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveClientRegistrationRepository clientRegistrationRepository
    ){
        return http.authorizeExchange(exchange -> exchange
                        .pathMatchers("/", "/*.css", "/*.js", "/favicon.ico").permitAll()
                        .pathMatchers(HttpMethod.GET, "/books/**").permitAll()
                        .anyExchange().authenticated())       // 그 외 다른 요청은 사용자 인증 필요
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(UNAUTHORIZED))) // 사용자가 인증되지 않을경우 401 반환
                .oauth2Login(Customizer.withDefaults())                                          // OAuth2/오픈ID 커넥트를 사용한 사용자 인증 활성화
                .logout(logout -> logout.logoutSuccessHandler(
                        oidcLogoutSuccessHandler(clientRegistrationRepository)))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())   // CSRF토큰을 교환하기위해 쿠기 기반 방식 사용
                        .csrfTokenRequestHandler(new XorServerCsrfTokenRequestAttributeHandler()::handle))
                .build();
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");                         // 애플리케이션 베이스 URL로 리다이렉션
        return oidcLogoutSuccessHandler;
    }

    /**
     * CsrfToken 리액티브 스트림을 구독하고
     * 이 토큰의 값을 올바르게 추출하기 위한 목적만을 갖는 필터
     * @return
     */
    @Bean
    WebFilter csrfWebFilter() {
        return (exchange, chain) -> {
            exchange.getResponse()
                    .beforeCommit(() -> Mono.defer(() -> {
                        Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
                        return csrfToken != null ? csrfToken.then() : Mono.empty();
                    }));
            return chain.filter(exchange);
        };
    }

}
