package com.example.meeting.reservation.controller;

import com.example.meeting.reservation.common.ApiResponse;
import com.example.meeting.reservation.common.EmployeeSuccessCode;
import com.example.meeting.reservation.dto.EmployeeUsernameCheckResponse;
import com.example.meeting.reservation.service.EmployeeService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class EmployeeRestController {
    private final EmployeeService employeeService;

    @GetMapping("/employees/check-username")
    public ResponseEntity<ApiResponse<EmployeeUsernameCheckResponse>> checkUsername(
            @RequestParam
            @NotBlank(message = "아이디를 입력해주세요.")
            @Size(min = 5, max = 20, message = "아이디는 5~20자입니다.")
            @Pattern(
                    regexp = "^(?=.*[a-z])[a-z0-9]+$",
                    message = "아이디는 영문 소문자와 숫자만 가능하며 영문은 필수입니다."
            )
            String username) {

        var available = employeeService.isUsernameAvailable(username);
        var response = new EmployeeUsernameCheckResponse(available);
        var code = EmployeeSuccessCode.CHECK_USERNAME;

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(code, response));
    }
}
