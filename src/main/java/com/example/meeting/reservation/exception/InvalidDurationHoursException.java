package com.example.meeting.reservation.exception;

public class InvalidDurationHoursException extends RuntimeException {
    public InvalidDurationHoursException(int hours) {
        super("이용 시간 %d시간은 유효하지 않습니다.".formatted(hours));
    }
}
