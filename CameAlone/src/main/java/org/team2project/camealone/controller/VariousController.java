package org.team2project.camealone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VariousController {

    @GetMapping("/login") //  로그인페이지
    public String getLogin(){
        return "Login";
    }

    @GetMapping("/date") //  날짜 선택 페이지
    public String getDate(){
        return "Date_select";
    }
}
