package com.example.meeting.reservation.exception;

public class ExceedMeetingRoomException extends RuntimeException {
    public ExceedMeetingRoomException(String message) {
        super(message);
    }
}
