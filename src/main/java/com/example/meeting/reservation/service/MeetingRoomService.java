package com.example.meeting.reservation.service;

import com.example.meeting.reservation.common.DatabaseConstraintName;
import com.example.meeting.reservation.common.ExceptionUtils;
import com.example.meeting.reservation.dto.PageResponse;
import com.example.meeting.reservation.common.StringNormalizer;
import com.example.meeting.reservation.dto.AdminMeetingRoomListItem;
import com.example.meeting.reservation.dto.MeetingRoomAddRequest;
import com.example.meeting.reservation.dto.MeetingRoomListItem;
import com.example.meeting.reservation.dto.MeetingRoomUpdateRequest;
import com.example.meeting.reservation.entity.MeetingRoom;
import com.example.meeting.reservation.exception.DuplicateMeetingRoomException;
import com.example.meeting.reservation.exception.MeetingRoomNotFoundException;
import com.example.meeting.reservation.repository.MeetingRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingRoomService {
    private final MeetingRoomRepository meetingRoomRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void addRoom(MeetingRoomAddRequest request) {
        var name = StringNormalizer.normalize(request.name());
        var floor = request.floor();
        var description = StringNormalizer.normalize(request.description());
        var capacity = request.capacity();
        var room = MeetingRoom.create(name, floor, description, capacity);

        try {
            meetingRoomRepository.save(room);
        } catch(DataIntegrityViolationException e) {
            var constraintName = ExceptionUtils.findConstraintName(e);
            if(DatabaseConstraintName.MeetingRoom.FLOOR_NAME.equals(constraintName)) {
                throw new DuplicateMeetingRoomException("이미 존재하는 회의실입니다.");
            }
            throw e;
        }
        log.info("회의실 추가 id={}", room.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateRoom(Long id, MeetingRoomUpdateRequest request) {
        var room = meetingRoomRepository.findById(id)
                .orElseThrow(() -> new MeetingRoomNotFoundException(id));
        var name = StringNormalizer.normalize(request.name());
        var floor = request.floor();
        var description = StringNormalizer.normalize(request.description());
        var capacity = request.capacity();

        room.updateRoom(name, floor, description, capacity);
        log.info("회의실 정보 수정 id={}", id);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public PageResponse<MeetingRoomListItem> getActiveRooms(Integer floor, Pageable pageable) {
        return meetingRoomRepository.getActiveRooms(floor, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<AdminMeetingRoomListItem> getAdminRooms(Integer floor, Boolean active, Pageable pageable) {
        return meetingRoomRepository.getAdminRooms(floor, active, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean isFloorNameAvailable(int floor, String name) {
        return !meetingRoomRepository.existsByFloorAndName(floor, name);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateActive(Long id, boolean active) {
        meetingRoomRepository.updateActive(id, active);
    }
}
