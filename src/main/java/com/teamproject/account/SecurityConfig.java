package com.teamproject.account;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //FilterChain : 모든유저의 요청과 서버의 응답사이에 자동으로 실행해주고 싶은코드를 담는곳

        http.csrf((csrf) -> csrf.disable());
        //csrf를 비활성화 하는코드 (테스트할떄는 끄자 귀차늠)

        http.authorizeHttpRequests((authorize) ->
                authorize.requestMatchers("/**").permitAll()
                //특정 페이지 로그인검사 할지말지 결정하는코드 .permitAll()함수는 아무나 접속허용 /**는 모든Url을 의미 즉
                //위코드는 모든 Url 에서 모든유저는 접속허용한다는 뜻
        );
        return http.build();
    }
}
