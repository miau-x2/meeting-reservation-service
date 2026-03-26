package com.example.meeting.reservation.common;

import com.example.meeting.reservation.entity.Employee;

public final class DatabaseConstraintName {
    private DatabaseConstraintName() {}

    public static final class Employee {
        private Employee() {}
        public static final String USERNAME = "employees.uk_employees_username";
    }
}
