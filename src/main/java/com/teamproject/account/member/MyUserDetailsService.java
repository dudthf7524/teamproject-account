package com.teamproject.account.member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService,OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //사용자가 제출한 아이디가 String username 으로 알아서들어감
 /*     DB에서 username을 가진 유저를 찾아와서
        return new User(유저아이디, 비번, 권한) 해주세요*/
        Optional<Member> member = memberRepository.findByUsername(username);
        if(!member.isPresent()){
            throw new UsernameNotFoundException("아이디를 찾을수없음");
        }
        var user = member.get();
        //권한을 집어넣을떄는 List타입이 GrantedAuthority 가 들어가야한다~~!
        List<GrantedAuthority> authority = new ArrayList<>();
        //권한을 추가할때는 new SimpleGrantedAuthority() 함수를 사용해야한다..
        authority.add(new SimpleGrantedAuthority("일반유저"));
        var a = new CustomUser(user.getUsername(), user.getPassword(), authority);
        a.memberNo = user.getMemberNo();
        return a;
    }

    public class CustomUser extends User {
        public Long memberNo;
        public CustomUser(String username,
                          String password,
                          List<GrantedAuthority> authorities ) {
            super(username, password, authorities);
        }

    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getAttribute("sub"); // Google의 유저 ID

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        Member member;

        if (memberOptional.isPresent()) {
            member = memberOptional.get();
        } else {
            // Google 계정을 통해 신규 사용자 등록
            member = new Member();
            member.setUsername(email);
            member.setEmail(email);
            member.setMemberName(name);
            member.setProviderId(providerId);
            member.setProvider("Google");
            memberRepository.save(member);
        }

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        // DefaultOAuth2User를 통해 OAuth2User와 UserDetails를 동시에 구현
        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "email");
    }





}
