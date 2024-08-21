package com.teamproject.account.calender;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalenderController {
    @GetMapping("/account/calender")
    public String calender(){

        return "/account/calender";
    }
}
