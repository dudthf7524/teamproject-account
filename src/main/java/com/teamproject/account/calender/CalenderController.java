package com.teamproject.account.calender;

import com.teamproject.account.income.IncomeDTO;
import com.teamproject.account.member.Login.MemberTypeCheck;
import com.teamproject.account.outcome.OutcomeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CalenderController {

    private final CalenderService calenderService;

    @GetMapping("/account/calender")
    public String calender(Model model, IncomeDTO incomeDTO, Authentication auth) {

        MemberTypeCheck memberTypeCheck = new MemberTypeCheck();
        Map<String, Object> result = memberTypeCheck.check(auth);
        Long memberNo = (Long) result.get("memberNo");

        if (incomeDTO.getRegDt() == null) {
            LocalDate today = LocalDate.now();
            String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            incomeDTO.setRegDt(formattedDate);
        }

        List<IncomeDTO> incomeDTOList = calenderService.findByIncome(memberNo, incomeDTO.getRegDt());
        List<OutcomeDTO> outcomeDTOList = calenderService.findByoutcome(memberNo, incomeDTO.getRegDt());

        int incometotalmonth = 0;
        int outcometotalmonth = 0;

        for (IncomeDTO income : incomeDTOList) {
            incometotalmonth += income.getPrice();
        }
        for (OutcomeDTO outcome : outcomeDTOList) {
            outcometotalmonth += outcome.getPrice();
        }
        String incometotalcomma = String.format("%,d", incometotalmonth);
        String outcometotalcomma = String.format("%,d", outcometotalmonth);
        // 모델에 현재 선택된 날짜를 추가
        model.addAttribute("name", "최영솔");
        model.addAttribute("searchedDate", incomeDTO.getRegDt());
        model.addAttribute("incometotalmonth", incometotalcomma);
        model.addAttribute("outcometotalmonth", outcometotalcomma);
        System.out.println(incomeDTO.getRegDt());
        // 추가적인 로직 (예: 수입/지출 조회)
        // 필요한 데이터들을 조회하여 모델에 추가

        return "/account/calender"; // View 이름 반환
    }

    @GetMapping("/api/financial-info")
    public ResponseEntity<Map<String, Object>> getFinancialInfo(@RequestParam("date") String date, Authentication auth) {
        MemberTypeCheck memberTypeCheck = new MemberTypeCheck();
        Map<String, Object> result = memberTypeCheck.check(auth);
        Long memberNo = (Long) result.get("memberNo");

        List<IncomeDTO> incomeDTOList = calenderService.findByIncome(memberNo, date);
        List<OutcomeDTO> outcomeDTOList = calenderService.findByoutcome(memberNo, date);

        int incometotal = 0;
        int outcometotal = 0;

        for (IncomeDTO income : incomeDTOList) {
            incometotal += income.getPrice();
        }
        for (OutcomeDTO outcome : outcomeDTOList) {
            outcometotal += outcome.getPrice();
        }

        String incometotalcomma = String.format("%,d", incometotal);
        String outcometotalcomma = String.format("%,d", outcometotal);
        Map<String, Object> data = new HashMap<>();
        data.put("date", date);
        data.put("incomeDTOList", incomeDTOList);
        data.put("outcomeDTOList", outcomeDTOList);
        data.put("incometotal", incometotalcomma);
        data.put("outcometotal", outcometotalcomma);

        return ResponseEntity.ok(data);
    }
}
