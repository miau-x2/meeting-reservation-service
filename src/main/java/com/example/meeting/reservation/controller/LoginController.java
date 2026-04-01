package com.example.meeting.reservation.controller;

import com.example.meeting.reservation.common.util.FlashMapKey;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginForm(Model model) {
        if (!model.containsAttribute(FlashMapKey.USERNAME)) {
            model.addAttribute(FlashMapKey.USERNAME, "");
        }
        return "employee/login";
    }
}
