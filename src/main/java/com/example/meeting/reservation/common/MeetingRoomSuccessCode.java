package com.example.meeting.reservation.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MeetingRoomSuccessCode implements ApiCode {
    CHECK_FLOOR_NAME("ROOM_200_001", "(층수, 이름) 중복 확인", HttpStatus.OK);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    MeetingRoomSuccessCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
