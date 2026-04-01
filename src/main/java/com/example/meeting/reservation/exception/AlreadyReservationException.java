package com.example.meeting.reservation.exception;

public class AlreadyReservationException extends RuntimeException {
    public AlreadyReservationException(String message) {
        super(message);
    }
}
