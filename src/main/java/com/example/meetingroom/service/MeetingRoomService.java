package com.example.meetingroom.service;

import com.example.meetingroom.dto.meetingRoom.MeetingRoomRequestDto;
import com.example.meetingroom.dto.meetingRoom.MeetingRoomResponseDto;
import com.example.meetingroom.entity.MeetingRoom;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.MeetingRoomRepository;
import com.example.meetingroom.util.CustomResponseEntity;
import com.example.meetingroom.util.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;

    public List<MeetingRoomResponseDto> getAllMeetingRooms() {
        return meetingRoomRepository.findAll().stream().map(MeetingRoomResponseDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public MeetingRoomResponseDto createMeetingRoom(final MeetingRoomRequestDto meetingRoomRequestDto) {
        if (meetingRoomRepository.existsByName(meetingRoomRequestDto.getName())) {
            throw new CustomException(ErrorCode.MEETING_ROOM_ALREADY_EXISTED_NAME);
        }

        MeetingRoom meetingRoom = MeetingRoom.toEntity(meetingRoomRequestDto);
        MeetingRoom newMeetingRoom = meetingRoomRepository.save(meetingRoom);
        return MeetingRoomResponseDto.fromEntity(newMeetingRoom);
    }

    @Transactional
    public MeetingRoomResponseDto updateMeetingRoom(final Long id, final MeetingRoomRequestDto meetingRoomRequestDto) {
        MeetingRoom foundMeetingRoom = meetingRoomRepository.findById(id).orElseThrow(
            () -> new CustomException(ErrorCode.MEETING_ROOM_NOT_FOUND)
        );
        foundMeetingRoom.update(meetingRoomRequestDto.getName(),
            meetingRoomRequestDto.getCapacity(),
            meetingRoomRequestDto.getPricePerHour());
        return MeetingRoomResponseDto.fromEntity(foundMeetingRoom);
    }

    @Transactional
    public void deleteMeetingRoom(final Long id) {
        meetingRoomRepository.findById(id).orElseThrow(
            () -> new CustomException(ErrorCode.MEETING_ROOM_NOT_FOUND)
        );
        meetingRoomRepository.deleteById(id);
    }
}
