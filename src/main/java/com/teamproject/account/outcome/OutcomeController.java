package com.teamproject.account.outcome;

import com.teamproject.account.income.IncomeDTO;
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
public class OutcomeController {
    private final OutcomeService outcomeService;

    @GetMapping("/outcome/write")
    public String write(Model model){
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        System.out.println(formattedDate);
        List<OutcomeDTO> outcomeDTOList = outcomeService.findAllByregDtContains(formattedDate);
        model.addAttribute("searchedDate", formattedDate);
        model.addAttribute("outcomeDTOList", outcomeDTOList);
        return "/outcome/write";
    }

    @PostMapping("/outcome/write")
    public String write(@ModelAttribute OutcomeDTO outcomeDTO, Authentication auth){
        MyUserDetailsService.CustomUser result = (MyUserDetailsService.CustomUser) auth.getPrincipal();

        outcomeDTO.setMemberNo(result.memberNo);
        outcomeService.save(outcomeDTO);
        String data = outcomeDTO.getRegDt();
        String dataupdate = data.substring(0,7);
        outcomeDTO.setRegDt(dataupdate);

        return "redirect:/outcome/list?regDt="+outcomeDTO.getRegDt();
    }

    @GetMapping("/outcome/list")
    public String list(Model model, @ModelAttribute OutcomeDTO outcomeDTO){
        if(outcomeDTO.getRegDt() == null){
            LocalDate today = LocalDate.now();
            String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            outcomeDTO.setRegDt(formattedDate);
        }
        List<OutcomeDTO> outcomeDTOList = outcomeService.findAllByregDtContains(outcomeDTO.getRegDt());

        model.addAttribute("outcomeDTOList", outcomeDTOList);
        model.addAttribute("searchedDate", outcomeDTO.getRegDt());
        model.addAttribute("type", "outcome");
        return "outcome/list";
    }

    @GetMapping("/outcome/update/{outcomeId}")
    public String update(@ModelAttribute OutcomeDTO outcomeDTO, Model model){
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern(outcomeDTO.getRegDt()));

        List<OutcomeDTO> outcomeDTOList = outcomeService.findAllByregDtContains(formattedDate);
        model.addAttribute("outcomeDTOList", outcomeDTOList);
        model.addAttribute("outcomeId", outcomeDTO.getOutcomeId());
        model.addAttribute("regDt", outcomeDTO.getRegDt());
        return "/outcome/update";
    }

    @PostMapping("/outcome/update")
    public String updateForm(@ModelAttribute OutcomeDTO outcomeDTO, Authentication auth){
        MyUserDetailsService.CustomUser result = (MyUserDetailsService.CustomUser) auth.getPrincipal();

        outcomeDTO.setMemberNo(result.memberNo);
        outcomeService.updateForm(outcomeDTO);
        String data = outcomeDTO.getRegDt();
        String dataupdate = data.substring(0,7);
        outcomeDTO.setRegDt(dataupdate);
        return "redirect:/outcome/list?regDt="+outcomeDTO.getRegDt();
    }

    @GetMapping("/outcome/delete/{outcomeId}")
    public String delete(@ModelAttribute OutcomeDTO outcomeDTO){
        outcomeService.deleteById(outcomeDTO.getOutcomeId());
        return "redirect:/outcome/list";
    }

    @PostMapping("/outcome/search")
    public String search(Model model, @ModelAttribute OutcomeDTO outcomeDTO){
        List<OutcomeDTO> outcomeDTOList = outcomeService.findAllByregDtContains(outcomeDTO.getRegDt());
        model.addAttribute("outcomeDTOList", outcomeDTOList);
        model.addAttribute("searchedDate", outcomeDTO.getRegDt());
        model.addAttribute("type", "outcome");
        return "/outcome/list";
    }
}
