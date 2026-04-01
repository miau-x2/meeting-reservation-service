package com.example.meeting.reservation.controller;

import com.example.meeting.reservation.authentication.CustomUserDetails;
import com.example.meeting.reservation.common.ApiResponse;
import com.example.meeting.reservation.common.ReservationSuccessCode;
import com.example.meeting.reservation.dto.AvailableRoomItemSlotResponse;
import com.example.meeting.reservation.dto.ReservationAddCommand;
import com.example.meeting.reservation.dto.ReservationAddRequest;
import com.example.meeting.reservation.dto.RoomTimeSlotItem;
import com.example.meeting.reservation.service.ReservationService;
import com.example.meeting.reservation.service.RoomTimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationRestController {
    private final ReservationService reservationService;
    private final RoomTimeSlotService roomTimeSlotService;

    @PostMapping("/rooms/{room-id}/reservations")
    public ResponseEntity<ApiResponse<Void>> addReservation(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("room-id") Long roomId, @Valid @RequestBody ReservationAddRequest request) {
        reservationService.addReservation(new ReservationAddCommand(
                userDetails.getId(),
                roomId,
                request.date(),
                request.startTime(),
                request.durationHours(),
                request.attendeeCount()
        ));

        var code = ReservationSuccessCode.MEETING_ROOM_RESERVED;
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.success(code));
    }

    @GetMapping("/rooms/{room-id}/slots")
    public ResponseEntity<ApiResponse<List<AvailableRoomItemSlotResponse>>> getAvailableSlots(@PathVariable("room-id") Long roomId, @RequestParam LocalDate date, @RequestParam("duration") int durationHours) {
        var response = roomTimeSlotService.getAvailableSlots(durationHours, roomId, date);
        var code = ReservationSuccessCode.MEETING_ROOM_AVAILABLE_TIME;

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.success(code, response));
    }
}
