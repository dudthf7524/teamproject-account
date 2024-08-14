package com.teamproject.account;
import com.teamproject.account.member.CustomAuthenticationFailureHandler;
import com.teamproject.account.member.CustomAuthenticationSuccessHandler;
import com.teamproject.account.member.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final MyUserDetailsService myUserDetailsService;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    public SecurityConfig(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

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
        http.formLogin((formLogin)
                -> formLogin.loginPage("/login")
                .successHandler(authenticationSuccessHandler())
                .failureHandler(customAuthenticationFailureHandler())
                //.failureUrl("/loginFail")
        );
        http.logout(logout
                -> logout
                .logoutUrl("/logout") //해당 url로 가면 로그아웃됨
                .logoutSuccessUrl("/") //로그아웃 성공시 페이지
                .invalidateHttpSession(true) // 세션 무효화
                .deleteCookies("JSESSIONID") // 쿠키 삭제
        );
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(myUserDetailsService) // OAuth2UserService 설정
                )
        );
        return http.build();
    }

    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomAuthenticationSuccessHandler();
    }

}
