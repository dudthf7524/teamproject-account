package com.teamproject.account.member;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public String join(Member member,String joinCode) throws Exception{
        Optional<Member> idCheck = memberRepository.findByUsername(member.getUsername());
        Map<String, String> errors = new HashMap<>();
        if(idCheck.isPresent()){ //DB의 ID중복체크
            errors.put("username", "존재하는 아이디입니다.");
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
}
