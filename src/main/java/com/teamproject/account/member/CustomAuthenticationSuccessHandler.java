package com.teamproject.account.member;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 로그인 성공한 id 값 가져오기
        String username = authentication.getName();

        // 가져온 id 값 활용하기
        System.out.println("Login successful for user: " + username);
        request.getSession().setAttribute("loginSuccessUsername", username);
        // 성공 후 리다이렉트 처리
        response.sendRedirect("/loginSuccess");
    }
}
