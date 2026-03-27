package com.example.meeting.reservation.repository;

import com.example.meeting.reservation.dto.PageResponse;
import com.example.meeting.reservation.dto.AdminMeetingRoomListItem;
import com.example.meeting.reservation.dto.MeetingRoomListItem;
import org.springframework.data.domain.Pageable;

public interface MeetingRoomRepositoryCustom {
    PageResponse<MeetingRoomListItem> getActiveRooms(Pageable pageable);
    PageResponse<AdminMeetingRoomListItem> getAdminRooms(Integer floor, Boolean active, Pageable pageable);
    void updateActive(Long id, boolean active);
}
