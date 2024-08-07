package com.teamproject.account.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    @GetMapping("/member/join")
    public String join() {
        return "member/join";
    }

    @GetMapping("/member/login")
    public String login() {
        return "member/login";
    }

}
