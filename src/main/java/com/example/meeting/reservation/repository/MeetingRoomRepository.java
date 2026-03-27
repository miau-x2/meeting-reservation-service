package com.example.meeting.reservation.repository;

import com.example.meeting.reservation.entity.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long>, MeetingRoomRepositoryCustom {
    boolean existsByFloorAndName(int floor, String name);
}
