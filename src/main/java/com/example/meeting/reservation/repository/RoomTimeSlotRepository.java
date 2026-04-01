package com.example.meeting.reservation.repository;

import com.example.meeting.reservation.entity.RoomTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomTimeSlotRepository extends JpaRepository<RoomTimeSlot, Long>, RoomTimeSlotRepositoryCustom {
}
