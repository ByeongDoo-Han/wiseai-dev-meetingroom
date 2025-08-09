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

@Service
@RequiredArgsConstructor
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;

    public List<MeetingRoomResponseDto> getAllMeetingRooms() {
        List<MeetingRoom> foundMeetingRoomList = meetingRoomRepository.findAll();
        List<MeetingRoomResponseDto> responseDtoList = new ArrayList<>();
        for(MeetingRoom meetingRoom:foundMeetingRoomList){
            MeetingRoomResponseDto dto = MeetingRoomResponseDto.builder()
                .id(meetingRoom.getId())
                .name(meetingRoom.getName())
                .pricePerHour(meetingRoom.getPricePerHour())
                .capacity(meetingRoom.getCapacity())
                .build();
            responseDtoList.add(dto);
        }
        return responseDtoList;
    }

    @Transactional
    public MeetingRoomResponseDto createMeetingRoom(final MeetingRoomRequestDto meetingRoomRequestDto) {
        if(meetingRoomRepository.existsByName(meetingRoomRequestDto.getName())){
            throw new CustomException(ErrorCode.MEETING_ROOM_ALREADY_EXISTED_NAME);
        }

        MeetingRoom meetingRoom = MeetingRoom.builder()
            .name(meetingRoomRequestDto.getName())
            .capacity(meetingRoomRequestDto.getCapacity())
            .pricePerHour(meetingRoomRequestDto.getPricePerHour())
            .build();
        MeetingRoom newMeetingRoom = meetingRoomRepository.save(meetingRoom);
        return newMeetingRoom.toResponseEntity();
    }

    @Transactional
    public MeetingRoomResponseDto updateMeetingRoom(final Long id, final MeetingRoomRequestDto meetingRoomRequestDto) {
        MeetingRoom foundMeetingRoom = meetingRoomRepository.findById(id).orElseThrow(
            () -> new CustomException(ErrorCode.MEETING_ROOM_NOT_FOUND)
        );
        foundMeetingRoom.update(meetingRoomRequestDto.getName(),
            meetingRoomRequestDto.getCapacity(),
            meetingRoomRequestDto.getPricePerHour());
        meetingRoomRepository.save(foundMeetingRoom);
        return foundMeetingRoom.toResponseEntity();
    }

    @Transactional
    public void deleteMeetingRoom(final Long id) {
        meetingRoomRepository.findById(id).orElseThrow(
            () -> new CustomException(ErrorCode.MEETING_ROOM_NOT_FOUND)
        );
        meetingRoomRepository.deleteById(id);
    }
}
