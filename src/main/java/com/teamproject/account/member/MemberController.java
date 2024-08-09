package com.teamproject.account.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/member/join")
    public String join() {
        return "member/join";
    }
    @PostMapping("/member/joinProc")
    @ResponseBody
    public ResponseEntity<?> join(
            @ModelAttribute Member member,
            @RequestParam("joinCode") String joinCode
            ) {
        try {
            String successMessage = memberService.join(member,joinCode);
            return ResponseEntity.ok(Map.of("message", successMessage));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/login")
    public String login() {
        return "member/login";
    }
    @GetMapping("/logout")
    public void logout(Authentication auth){

    }




}
