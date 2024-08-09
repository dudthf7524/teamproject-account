package com.teamproject.account.member;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {
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
        return new User(user.getUsername(),user.getPassword(),authority);
    }
}
