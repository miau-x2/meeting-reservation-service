package com.example.meeting.reservation.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReservationSuccessCode implements ApiCode {
    MEETING_ROOM_RESERVED("RESERVATION_201_001", "회의실 예약 완료", HttpStatus.CREATED),
    MEETING_ROOM_AVAILABLE_TIME("RESERVATION_200_001", "회의실 예약 가능 시간 조회", HttpStatus.OK);
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ReservationSuccessCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
