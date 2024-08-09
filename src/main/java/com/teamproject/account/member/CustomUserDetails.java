package com.teamproject.account.member;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Arrays;
import java.util.Map;

public class CustomUserDetails extends User implements OAuth2User {
    private Map<String, Object> attributes;

    public CustomUserDetails(Member member, Map<String, Object> attributes) {
        super(member.getUsername(), member.getPassword(), Arrays.asList(new SimpleGrantedAuthority("일반유저")));
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return this.getUsername();
    }
}
