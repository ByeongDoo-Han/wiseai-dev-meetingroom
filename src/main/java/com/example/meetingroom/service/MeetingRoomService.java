package com.example.meetingroom.service;

import com.example.meetingroom.dto.MeetingRoomRequestDto;
import com.example.meetingroom.dto.MeetingRoomResponseDto;
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

    public ResponseEntity<CustomResponseEntity<Object>> getAllMeetingRooms() {
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
        return ResponseEntity.ok().body(
            CustomResponseEntity.builder()
                .success(true)
                .httpStatus(SuccessMessage.GET_MEETING_ROOM_SUCCESS.getHttpStatus())
                .message(SuccessMessage.GET_MEETING_ROOM_SUCCESS.getMessage())
                .data(responseDtoList).build()
        );
    }

    @Transactional
    public ResponseEntity<CustomResponseEntity<Object>> createMeetingRoom(final MeetingRoomRequestDto meetingRoomRequestDto) {
        if(meetingRoomRepository.existsByName(meetingRoomRequestDto.getName())){
            throw new CustomException(ErrorCode.MEETING_ROOM_ALREADY_EXISTED_NAME);
        }

        MeetingRoom meetingRoom = MeetingRoom.builder()
            .name(meetingRoomRequestDto.getName())
            .capacity(meetingRoomRequestDto.getCapacity())
            .pricePerHour(meetingRoomRequestDto.getPricePerHour())
            .build();
        MeetingRoom newMeetingRoom = meetingRoomRepository.save(meetingRoom);
        return ResponseEntity.ok().body(CustomResponseEntity.builder()
            .data(newMeetingRoom.toResponseEntity())
            .httpStatus(SuccessMessage.GET_MEETING_ROOM_SUCCESS.getHttpStatus())
            .message(SuccessMessage.GET_MEETING_ROOM_SUCCESS.getMessage())
            .build());
    }

    @Transactional
    public ResponseEntity<CustomResponseEntity<Object>> updateMeetingRoom(final Long id, final MeetingRoomRequestDto meetingRoomRequestDto) {
        MeetingRoom foundMeetingRoom = meetingRoomRepository.findById(id).orElseThrow(
            () -> new CustomException(ErrorCode.MEETING_ROOM_NOT_FOUND)
        );
        return ResponseEntity.ok().body(CustomResponseEntity.builder()
            .data(foundMeetingRoom.toResponseEntity())
            .httpStatus(SuccessMessage.UPDATE_MEETING_ROOM_SUCCESS.getHttpStatus())
            .message(SuccessMessage.UPDATE_MEETING_ROOM_SUCCESS.getMessage())
            .build());
    }

    @Transactional
    public ResponseEntity<CustomResponseEntity<Object>> deleteMeetingRoom(final Long id) {
        meetingRoomRepository.findById(id).orElseThrow(
            () -> new CustomException(ErrorCode.MEETING_ROOM_NOT_FOUND)
        );
        meetingRoomRepository.deleteById(id);
        return ResponseEntity.ok().body(CustomResponseEntity.builder()
            .data(null)
            .message(SuccessMessage.DELETE_MEETING_ROOM_SUCCESS.getMessage())
            .httpStatus(SuccessMessage.DELETE_MEETING_ROOM_SUCCESS.getHttpStatus())
            .build());
    }
}
