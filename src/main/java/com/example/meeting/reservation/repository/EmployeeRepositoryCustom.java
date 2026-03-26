package com.example.meeting.reservation.repository;

import java.time.LocalDateTime;

public interface EmployeeRepositoryCustom {
    void updateLastLoginAt(String username, LocalDateTime lastLoginAt);
}
