package com.example.meeting.reservation.controller;

import com.example.meeting.reservation.dto.EmployeeCreateRequest;
import com.example.meeting.reservation.exception.DuplicateEmployeeUsernameException;
import com.example.meeting.reservation.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private static final String SIGNUP_VIEW_NAME = "employee/signup";

    private final EmployeeService employeeService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        if (!model.containsAttribute("signupFormData")) {
            model.addAttribute("signupFormData", EmployeeCreateRequest.empty());
        }
        return SIGNUP_VIEW_NAME;
    }

    @PostMapping("/signup")
    public String signup(
            @Valid @ModelAttribute("signupFormData") EmployeeCreateRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return SIGNUP_VIEW_NAME;
        }

        try {
            employeeService.signup(request);
            return "redirect:/";
        } catch (DuplicateEmployeeUsernameException e) {
            bindingResult.rejectValue("username", "username.duplicate", e.getMessage());
            return SIGNUP_VIEW_NAME;
        }
    }
}
