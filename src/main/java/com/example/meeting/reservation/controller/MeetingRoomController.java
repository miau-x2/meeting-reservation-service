package com.example.meeting.reservation.controller;

import com.example.meeting.reservation.service.MeetingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MeetingRoomController {
    private final MeetingRoomService meetingRoomService;

    @GetMapping("/rooms")
    public String getActiveRooms(
            Pageable pageable,
            @RequestParam(required = false) Integer floor,
            Model model) {
        model.addAttribute("roomsPage", meetingRoomService.getActiveRooms(floor, pageable));
        model.addAttribute("selectedFloor", floor);
        return "meeting/rooms";
    }
}
