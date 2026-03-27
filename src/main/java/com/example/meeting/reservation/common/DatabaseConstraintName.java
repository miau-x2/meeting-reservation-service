package com.example.meeting.reservation.common;

public final class DatabaseConstraintName {
    private DatabaseConstraintName() {}

    public static final class Employee {
        private Employee() {}
        public static final String USERNAME = "employees.uk_employees_username";
    }

    public static final class MeetingRoom {
        private MeetingRoom() {}
        public static final String FLOOR_NAME = "meeting_rooms.uk_meeting_rooms_floor_name";
    }
}
