package com.example.meeting.reservation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "meeting_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingRoom extends BaseEntity {
    @NotBlank
    @Size(max = 50)
    private String name;
    @Min(1)
    @Max(10)
    private int floor;
    @NotBlank
    @Size(max = 100)
    private String description;
    @Min(1)
    @Max(200)
    private int capacity;
    private boolean active;

    private MeetingRoom(String name, int floor, String description, int capacity, boolean active) {
        this.name = name;
        this.floor = floor;
        this.description = description;
        this.capacity = capacity;
        this.active = active;
    }

    public static MeetingRoom create(String name, int floor, String description, int capacity) {
        return new MeetingRoom(name, floor, description, capacity, true);
    }

    public void updateRoom(String name, int floor, String description, int capacity) {
        this.name = name;
        this.floor = floor;
        this.description = description;
        this.capacity = capacity;
    }
}
