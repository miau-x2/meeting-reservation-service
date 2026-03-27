package com.example.meeting.reservation.controller;

import com.example.meeting.reservation.dto.MeetingRoomAddRequest;
import com.example.meeting.reservation.dto.MeetingRoomUpdateRequest;
import com.example.meeting.reservation.exception.DuplicateMeetingRoomException;
import com.example.meeting.reservation.service.MeetingRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMeetingRoomController {
    private static final String ROOMS_VIEW_NAME = "admin/rooms";

    private final MeetingRoomService meetingRoomService;

    @GetMapping("/rooms")
    public String getAllRooms(
            Pageable pageable,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Boolean active,
            Model model) {
        populateRoomsPage(model, pageable, floor, active, false, false, null);
        return ROOMS_VIEW_NAME;
    }

    @PostMapping("/rooms")
    public String addMeetingRoom(
            Pageable pageable,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Boolean active,
            @Valid @ModelAttribute("addRoomFormData") MeetingRoomAddRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            populateRoomsPage(model, pageable, floor, active, true, false, null);
            return ROOMS_VIEW_NAME;
        }

        try {
            meetingRoomService.addRoom(request);
        } catch (DuplicateMeetingRoomException e) {
            bindingResult.reject("meetingRoom.duplicate", e.getMessage());
            populateRoomsPage(model, pageable, floor, active, true, false, null);
            return ROOMS_VIEW_NAME;
        }

        redirectAttributes.addFlashAttribute("roomSuccessMessage", "회의실이 추가되었습니다.");
        return buildRedirectUrl(pageable, floor, active);
    }

    @PostMapping("/rooms/{id}")
    public String updateMeetingRoom(
            @PathVariable Long id,
            Pageable pageable,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Boolean active,
            @Valid @ModelAttribute("updateRoomFormData") MeetingRoomUpdateRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            populateRoomsPage(model, pageable, floor, active, false, true, id);
            return ROOMS_VIEW_NAME;
        }

        meetingRoomService.updateRoom(id, request);
        redirectAttributes.addFlashAttribute("roomSuccessMessage", "회의실 정보가 수정되었습니다.");
        return buildRedirectUrl(pageable, floor, active);
    }

    @PostMapping("/rooms/{id}/active")
    public String updateMeetingRoomActive(
            @PathVariable Long id,
            Pageable pageable,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Boolean active,
            @RequestParam boolean nextActive,
            RedirectAttributes redirectAttributes) {
        meetingRoomService.updateActive(id, nextActive);
        redirectAttributes.addFlashAttribute(
                "roomSuccessMessage",
                nextActive ? "회의실이 활성 상태로 변경되었습니다." : "회의실이 비활성 상태로 변경되었습니다."
        );
        return buildRedirectUrl(pageable, floor, active);
    }

    private void populateRoomsPage(
            Model model,
            Pageable pageable,
            Integer floor,
            Boolean active,
            boolean openAddModal,
            boolean openEditModal,
            Long editingRoomId) {
        if (!model.containsAttribute("addRoomFormData")) {
            model.addAttribute("addRoomFormData", MeetingRoomAddRequest.empty());
        }
        if (!model.containsAttribute("updateRoomFormData")) {
            model.addAttribute("updateRoomFormData", MeetingRoomUpdateRequest.empty());
        }

        var roomsPage = meetingRoomService.getAdminRooms(floor, active, pageable);

        model.addAttribute("roomsPage", roomsPage);
        model.addAttribute("selectedFloor", floor);
        model.addAttribute("selectedActive", active);
        model.addAttribute("openAddModal", openAddModal);
        model.addAttribute("openEditModal", openEditModal);
        model.addAttribute("editingRoomId", editingRoomId);
    }

    private String buildRedirectUrl(Pageable pageable, Integer floor, Boolean active) {
        StringBuilder url = new StringBuilder(
                "redirect:/admin/rooms?page=%d&size=%d".formatted(pageable.getPageNumber() + 1, pageable.getPageSize())
        );
        if (floor != null) {
            url.append("&floor=").append(floor);
        }
        if (active != null) {
            url.append("&active=").append(active);
        }
        return url.toString();
    }
}
