package com.example.meetingroom.controller;

import com.example.meetingroom.dto.meetingRoom.MeetingRoomRequestDto;
import com.example.meetingroom.service.MeetingRoomService;
import com.example.meetingroom.util.CustomResponseEntity;
import com.example.meetingroom.util.ResponseUtil;
import com.example.meetingroom.util.SuccessMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Meeting Room API", description = "회의실 정보 조회 및 관리 API")
@RestController
@RequiredArgsConstructor
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    @Operation(
        summary = "모든 회의실 조회",
        description = "시스템에 등록된 모든 회의실 정보를 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "회의실 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CustomResponseEntity.class),
                    examples = @ExampleObject(
                        name = "회의실 목록 예시",
                        summary = "성공 응답 예시",
                        value = """
                            {
                              "success": true,
                              "message": "회의실 목록 조회 성공",
                              "httpStatus" : "OK",
                              "data": [
                                {
                                  "id": 1,
                                  "name": "A룸",
                                  "capacity": 10,
                                  "pricePerHour": 30000
                                },
                                {
                                  "id": 2,
                                  "name": "B룸",
                                  "capacity": 20,
                                  "pricePerHour": 45000
                                }
                              ]
                            }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "회의실 목록 조회 실패",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CustomResponseEntity.class),
                    examples = @ExampleObject(
                        name = "회의실 목록 조회 실패 예시",
                        summary = "실패 응답 예시",
                        value = """
                            {
                              "success": false,
                              "httpStatus" : "BAD_REQUEST",
                              "message": "회의실 목록 조회 실패",
                              "data": []
                            }
                            """
                    )
                )
            )
        }
    )
    @GetMapping("/meeting-rooms")
    public ResponseEntity<CustomResponseEntity<Object>> getAllMeetingRooms() {
        return ResponseUtil.success(
            meetingRoomService.getAllMeetingRooms(),
            SuccessMessage.GET_MEETING_ROOM_SUCCESS
        );
    }

    @Operation(summary = "새 회의실 생성", description = "새로운 회의실 정보를 시스템에 등록합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "회의실 생성 성공",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CustomResponseEntity.class),
                    examples = @ExampleObject(name = "회의실 생성 성공 예시", summary = "성공 응답",
                        value = """
                            {
                              "success": true,
                              "httpStatus": "CREATED",
                              "message": "회의실 생성 성공",
                              "data": [
                                {
                                    "id": 3,
                                    "name": "C룸",
                                    "capacity": 15,
                                    "pricePerHour": 35000
                                }
                              ]
                            }
                            """
                    ))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 유효성 검사 실패)",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CustomResponseEntity.class),
                    examples = @ExampleObject(name = "회의실 생성 실패 예시", summary = "실패 응답",
                        value = """
                            {
                              "success": false,
                              "httpStatus": "BAD_REQUEST",
                              "message": "회의실 생성 실패",
                              "data": []
                            }
                            """
                    ))),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 데이터",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CustomResponseEntity.class),
                    examples = @ExampleObject(name = "회의실 생성 실패 예시 (유효성 검사)", summary = "실패 응답",
                        value = """
                            {
                              "success": false,
                              "httpStatus": "CONFLICT",
                              "message": "이미 존재하는 회의실 이름입니다.",
                              "data": []
                            }
                            """
                    ))),
        })
    @PostMapping("/meeting-rooms")
    public ResponseEntity<CustomResponseEntity<Object>> createMeetingRoom(@RequestBody MeetingRoomRequestDto meetingRoomRequestDto) {
        return ResponseUtil.success(
            meetingRoomService.createMeetingRoom(meetingRoomRequestDto),
            SuccessMessage.CREATE_MEETING_ROOM_SUCCESS
        );
    }

    @Operation(summary = "회의실 정보 업데이트", description = "특정 ID의 회의실 정보를 업데이트합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "회의실 정보 업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "회의실을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 유효성 검사 실패)"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
        })
    @PutMapping("/meeting-rooms/{id}")
    public ResponseEntity<CustomResponseEntity<Object>> updateMeetingRoom(@PathVariable Long id,
                                                                    @RequestBody MeetingRoomRequestDto meetingRoomRequestDto) {
        return ResponseUtil.success(
            meetingRoomService.updateMeetingRoom(id,meetingRoomRequestDto),
            SuccessMessage.UPDATE_MEETING_ROOM_SUCCESS
        );
    }

    @Operation(summary = "회의실 삭제", description = "특정 ID의 회의실을 시스템에서 삭제합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "회의실 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "회의실을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
        })
    @DeleteMapping("/meeting-rooms/{id}")
    public ResponseEntity<CustomResponseEntity<Object>> deleteMeetingRoom(@PathVariable Long id) {
        meetingRoomService.deleteMeetingRoom(id);
        return ResponseUtil.success(
            null,
            SuccessMessage.DELETE_MEETING_ROOM_SUCCESS
        );
    }
}