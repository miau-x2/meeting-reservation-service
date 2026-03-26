package com.example.meeting.reservation.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum EmployeeSuccessCode implements ApiCode {
    CHECK_USERNAME("EMPLOYEE_200_001", "아이디 중복 확인", HttpStatus.OK);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    EmployeeSuccessCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
