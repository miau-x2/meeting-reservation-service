package com.example.meeting.reservation.common;

import org.springframework.http.HttpStatus;

public interface ApiCode {
    String getCode();
    String getMessage();
    HttpStatus getHttpStatus();
}
