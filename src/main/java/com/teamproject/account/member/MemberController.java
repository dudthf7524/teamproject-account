package com.teamproject.account.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailTokenRepository emailTokenRepository;
    private final S3Service s3Service;
//회원가입 ==============================================================================================================
    @GetMapping("/member/join")
    public String join(Authentication auth) {
        return "member/join";
    }

    @PostMapping("/member/joinProc")
    @ResponseBody
    public ResponseEntity<?> join(
            @ModelAttribute Member member,
            @RequestParam("joinCode") String joinCode
            ) {
        try {
            System.out.println("파일명: "+member.getMemberFile());
            String successMessage = memberService.join(member,joinCode);
            return ResponseEntity.ok(Map.of("message", successMessage,"email",member.getEmail()));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
//이메일 인증=============================================================================================================
    //회원가입시 이메일 처리
    @PostMapping("/member/email-verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String email) throws Exception {
        try{
            memberService.emailCheck(email); // 이미 존재하는 이메일인지 확인
        }catch (ValidationException e){
            return ResponseEntity.badRequest().body(e.getErrors());
        }
        // 이메일 인증 토큰 생성 및 발송
        String token = generateAlphaNumericToken().toString();
        memberService.sendVerificationEmail(email, token);
        System.out.println("이메일토큰값: "+token);
        // 토큰 저장
        EmailToken emailToken = new EmailToken(token, email);
        emailTokenRepository.save(emailToken);
        return ResponseEntity.ok(Map.of("message", "이메일 인증 링크가 발송되었습니다.","token", token));
    }

    //로그인시 이메일 처리
    @PostMapping("/member/email-verify2")
    public ResponseEntity<?> verifyEmail2(@RequestParam String email) throws Exception {
        try{
            memberService.emailCheck2(email); // 이미 존재하는 이메일인지 확인
        }catch (ValidationException e){
            return ResponseEntity.badRequest().body(e.getErrors());
        }
        // 이메일 인증 토큰 생성 및 발송
        String token = generateAlphaNumericToken().toString();
        memberService.sendVerificationEmail(email, token);
        System.out.println("이메일토큰값: "+token);
        // 토큰 저장
        EmailToken emailToken = new EmailToken(token, email);
        emailTokenRepository.save(emailToken);
        return ResponseEntity.ok(Map.of("message", "이메일 인증 링크가 발송되었습니다.","token", token));
    }
    //로그인시 이메일코드 확인
    @PostMapping("/member/email-token-verify")
    public ResponseEntity<?> emailTokenVerify(@RequestParam String email,@RequestParam String token){
        try {
            memberService.emailTokenVerify(email,token);
            return ResponseEntity.ok(Map.of("message", "인증완료"));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    //이메일 토큰6자리 랜덤생성메소드
    public String generateAlphaNumericToken() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            token.append(characters.charAt(index));
        }
        return token.toString();
    }
    @GetMapping("/joinProc2/{email}")
    public String joinProc2(@PathVariable String email){
        memberService.emailTokenDelete(email);
        return "redirect:/login";
    }
//로그인 처리=============================================================================================================
    @GetMapping("/login")
    public String login() {
        return "member/login";
    }
    @GetMapping("/loginFail")
    public String loginFail(RedirectAttributes redirectAttributes, HttpSession session){
        String username = (String) session.getAttribute("loginFailUsername");
        if(!username.isEmpty()){
            int loginCount = memberService.loginFailCount(username);
            redirectAttributes.addFlashAttribute("loginCount", "남은 로그인 기회: "+(5-loginCount)+"회");
            redirectAttributes.addFlashAttribute("count",loginCount);
            if(loginCount == 5){
                String email = memberService.emailSearch(username);
                redirectAttributes.addFlashAttribute("email",email);
            }
        }
        return "redirect:/login?error";
    }
    @GetMapping("/loginSuccess")
    public String loginSuccess(HttpSession session){
        String username = (String) session.getAttribute("loginSuccessUsername");
        memberService.loginSuccessCount(username);
        return "index";
    }
    @GetMapping("/logout")
    public void logout(Authentication auth){
    }

//회원 마이페이지=========================================================================================================
    @GetMapping("/member/mypage")
    public String myPage(){

        return "member/mypage.html";
    }

//S3버킷에 파일저장=======================================================================================================
    @GetMapping("/presigned-url")
    @ResponseBody
    String getURL(@RequestParam String filename){
        var result = s3Service.createPresignedUrl("test/"+filename);
        System.out.println("S3URL: "+result);
        return result;
    }

}
