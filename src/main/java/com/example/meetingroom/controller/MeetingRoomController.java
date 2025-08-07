package com.example.meetingroom.controller;

import com.example.meetingroom.entity.MeetingRoom;
import com.example.meetingroom.service.MeetingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    @GetMapping("/meeting-rooms")
    public List<MeetingRoom> getAllMeetingRooms() {
        return meetingRoomService.getAllMeetingRooms();
    }
}
