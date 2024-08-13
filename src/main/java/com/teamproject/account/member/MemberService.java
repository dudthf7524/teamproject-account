package com.teamproject.account.member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailTokenRepository emailTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;


    public String join(Member member,String joinCode) throws Exception{
        Optional<Member> idCheck = memberRepository.findByUsername(member.getUsername());
        Optional<Member> emailCheck = memberRepository.findByEmail(member.getEmail());
        Optional<EmailToken> emailTokenCheck = emailTokenRepository.findByEmail(member.getEmail());

        Map<String, String> errors = new HashMap<>();
        if(idCheck.isPresent()){ //DB의 ID중복체크
            errors.put("username", "등록된 아이디입니다.");
        }
        if(emailCheck.isPresent()){
            errors.put("email", "등록된 이메일입니다.");
        }
        if(joinCode.equals("ok")) {
            //이메일 토큰확인
        if (emailTokenCheck.isPresent()) {
            if (!emailTokenCheck.get().getToken().equals(member.getEmailTokenInput())) {
                errors.put("emailTokenInput", "이메일 인증번호가 틀렸습니다.");
            }
        } else {
            if (!errors.containsKey("email")) {
                    errors.put("email", "이메일 인증은 필수입니다.");
                }
            }
        }
        errors = nullCheck(member,errors); // join에서 넘어오는 널,공백체크
        errors = checkConstraint(member,errors); //join에서 넘어오는값들 제약조건을 체크
        System.out.println("에러메시지: "+errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors); //예외발생한것들 모아서 ValidationException class에 예외던지기
        }
        member.setPassword(passwordEncoder.encode(member.getPassword())); //비밀번호 암호화
        if(joinCode.equals("ok")){
            memberRepository.save(member); //DB저장
        }
        return "회원가입이 성공적으로 완료되었습니다.";
    }
//회원정보 NULL,공백 체크=============================================================================================
    private static Map<String, String> nullCheck (Member member,Map<String, String> errors) throws Exception {
        if(!errors.containsKey("username")) {
            validateField(member.getUsername(), "username/아이디는 필수입력입니다.", errors);
        }
        validateField(member.getPassword(),"password/비밀번호는 필수입력입니다.",errors);
        validateField(member.getEmail(), "email/이메일은 필수입력입니다.",errors);
        validateField(member.getMemberName(), "memberName/이름은 필수입력입니다.",errors);
        return errors;
    }
    private static void validateField(String field, String errorMessage,Map<String,String> errors) {
        int index = errorMessage.indexOf("/");
        String cutStr = errorMessage.substring(index);
        if (field == null || field.trim().isEmpty()) {
            errors.put(errorMessage.substring(0,index), errorMessage.substring(index+1));
        }
    }
//회원정보 제약조건 체크=============================================================================================
    private static Map<String, String> checkConstraint(Member member,Map<String,String> errors) throws ValidationException{
        //errors.containsKey("password")는
        // 주어진 키(예: "password")가 errors 맵에 존재하는지 여부를 확인하는 함수임
        if(!errors.containsKey("username")){
            //회원아이디의 영소문자,숫자,문자열의 공백을 확인한다.
            if(!member.getUsername().matches("[a-z0-9]{4,20}")){
                errors.put("username", "4~20자의 영문 소문자, 숫자만 사용 가능합니다.");
            }
        }
        if(!errors.containsKey("password")) {
            //회원비밀번호의 영대/소문자,숫자,문자열의 공백을 확인한다.
            if (!member.getPassword().matches("[a-zA-Z0-9]{8,16}")) {
                errors.put("password", "8~16자의 영문 대/소문자, 숫자만 사용 가능합니다.");
            }
            //비밀번호 확인과 비밀번호가 동일한값인지 체크합니다.
            if (!member.getPassword().equals(member.getPasswordChk()) ){
                errors.put("passwordChk", "비밀번호와 다릅니다.");
            }
        }
        if(!errors.containsKey("email")){
            //회원이메일값 체크
            if(!member.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
                errors.put("email", "이메일을 다시 입력해주세요.");
            }
        }
        return errors;
    }


    //이메일 인증관련
    public Optional<Member> emailCheck(String email) throws Exception{
        Optional<Member> result = memberRepository.findByEmail(email);
        Map<String, String> errors = new HashMap<>();
        if(result.isPresent()){
            errors.put("email", "등록된 이메일입니다.");
            throw new ValidationException(errors);
        }
        Optional<EmailToken> emailTokenCheck = emailTokenRepository.findByEmail(email);
        if(emailTokenCheck.isPresent()){
            emailTokenRepository.delete(emailTokenCheck.get());
        }
        return result;
    }

    public void sendVerificationEmail(String email, String token) {
        // 이메일 제목
        String subject = "이메일 인증을 완료해주세요";
        // 인증 링크 생성
        String confirmationUrl = token;
        // 이메일 본문 내용
        String message = "회원가입을 완료하려면 다음 인증번호를 입력해주세요 : " + confirmationUrl;
        // 이메일 객체 생성
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject(subject);
        emailMessage.setText(message);
        emailMessage.setFrom(fromAddress);
        System.out.println("이메일정보: "+emailMessage);
        // 이메일 전송
        mailSender.send(emailMessage);
    }

}
