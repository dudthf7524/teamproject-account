package com.teamproject.account.income;

import com.teamproject.account.member.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor

public class IncomeController {

    private final IncomeService incomeService;

    @GetMapping("/income/write")
    public String write(Model model){
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        System.out.println(formattedDate);
        List<IncomeDTO> incomeDTOList = incomeService.findAllByregDtContains(formattedDate);
        String year = formattedDate.substring(0,4);
        String month = formattedDate.substring(5,7);
        System.out.println(year);
        System.out.println(month);
        model.addAttribute("searchedDate", formattedDate);
        model.addAttribute("incomeDTOList", incomeDTOList);
        return "/income/write";
    }

    @PostMapping("/income/write")
    public String write(@ModelAttribute IncomeDTO incomeDTO, Authentication auth){
        MyUserDetailsService.CustomUser result = (MyUserDetailsService.CustomUser) auth.getPrincipal();
        System.out.println(result.memberNo);
        incomeDTO.setMemberNo(result.memberNo);
        incomeService.save(incomeDTO);

        System.out.println("accountDTO = " + incomeDTO);

        return "redirect:/income/write";
    }

    @GetMapping("/income/list")
    public String list(Model model){
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        System.out.println(formattedDate);
        List<IncomeDTO> incomeDTOList = incomeService.findAllByregDtContains(formattedDate);

        model.addAttribute("incomeDTOList", incomeDTOList);
        return "/income/list";
    }
}
