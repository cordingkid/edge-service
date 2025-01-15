package com.polarbookshop.edgeservice.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class UserController {

    /**
     * 현재 인증된 사용자에 대한 정보 반환
     * @param oidcUser
     * @return
     */
    @GetMapping("user")
    public Mono<User> getUser(@AuthenticationPrincipal OidcUser oidcUser) {
        var user = new User(
                oidcUser.getPreferredUsername(),
                oidcUser.getGivenName(),
                oidcUser.getFamilyName(),
                oidcUser.getClaimAsStringList("roles")      // roles 클레임을 추출해 문자열읠 리스트로 가져옴
        );
        return Mono.just(user);
    }

    /*
    // 이를 간단하게 위에걸로 구현가능
    @GetMapping("user")
    public Mono<User> getUser() {
        // ReactiveSecurityContextHolder 에서 현재 인증된 사용자에 대한 SecurityContext 가져온다.
        // SecurityContext로 부터 authentication 받고
        // authentication에서 Principal을 가져옴 OIDC 경우 OidcUser
        // OidcUser의 ID 토큰에서 추출한 데이터를 사용해 User 객체 생성
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (OidcUser) authentication.getPrincipal())
                .map(oidcUser -> new User(
                        oidcUser.getPreferredUsername(),
                        oidcUser.getGivenName(),
                        oidcUser.getFamilyName(),
                        List.of("employee", "customer")
                ));
    }
     */
}
