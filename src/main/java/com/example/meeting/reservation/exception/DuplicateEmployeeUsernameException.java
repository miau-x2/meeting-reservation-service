package com.example.meeting.reservation.exception;

public class DuplicateEmployeeUsernameException extends RuntimeException {
    public DuplicateEmployeeUsernameException(String message) {
        super(message);
    }
}
