package com.example.meetingroom.controller;

import com.example.meetingroom.dto.MeetingRoomRequestDto;
import com.example.meetingroom.dto.MeetingRoomResponseDto;
import com.example.meetingroom.service.MeetingRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Meeting Room API", description = "회의실 정보 조회 및 관리 API")
@RestController
@RequiredArgsConstructor
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    @Operation(summary = "모든 회의실 조회", description = "시스템에 등록된 모든 회의실 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회의실 목록 조회 성공"),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생")
            })
    @GetMapping("/meeting-rooms")
    public ResponseEntity<List<MeetingRoomResponseDto>> getAllMeetingRooms() {
        return meetingRoomService.getAllMeetingRooms();
    }

    @Operation(summary = "새 회의실 생성", description = "새로운 회의실 정보를 시스템에 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회의실 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 유효성 검사 실패)"),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생")
            })
    @PostMapping("/meeting-rooms")
    public ResponseEntity<MeetingRoomResponseDto> createMeetingRoom(@RequestBody MeetingRoomRequestDto meetingRoomRequestDto){
        return meetingRoomService.createMeetingRoom(meetingRoomRequestDto);
    }

    @Operation(summary = "회의실 정보 업데이트", description = "특정 ID의 회의실 정보를 업데이트합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회의실 정보 업데이트 성공"),
                    @ApiResponse(responseCode = "404", description = "회의실을 찾을 수 없음"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 유효성 검사 실패)"),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생")
            })
    @PutMapping("/meeting-rooms/{id}")
    public ResponseEntity<MeetingRoomResponseDto> updateMeetingRoom(@PathVariable Long id,
                                                                    @RequestBody MeetingRoomRequestDto meetingRoomRequestDto){
        return meetingRoomService.updateMeetingRoom(id, meetingRoomRequestDto);
    }

    @Operation(summary = "회의실 삭제", description = "특정 ID의 회의실을 시스템에서 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회의실 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "회의실을 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생")
            })
    @DeleteMapping("/meeting-rooms/{id}")
    public ResponseEntity<Void> deleteMeetingRoom(@PathVariable Long id){
        return meetingRoomService.deleteMeetingRoom(id);
    }
}