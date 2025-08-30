package com.example.meetingroom.service;

import com.example.meetingroom.dto.meetingRoom.MeetingRoomRequestDto;
import com.example.meetingroom.dto.meetingRoom.MeetingRoomResponseDto;
import com.example.meetingroom.entity.MeetingRoom;
import com.example.meetingroom.exception.CustomException;
import com.example.meetingroom.exception.ErrorCode;
import com.example.meetingroom.repository.MeetingRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeetingRoomService 테스트")
public class MeetingRoomServiceTest {

    @Mock
    private MeetingRoomRepository meetingRoomRepository;

    @InjectMocks
    private MeetingRoomService meetingRoomService;

    private MeetingRoom room1;
    private MeetingRoom room2;

    @BeforeEach
    void setUp() {
        room1 = MeetingRoom.createForTest(1L, "회의실 A", 10, BigDecimal.valueOf(10000));
        room2 = MeetingRoom.createForTest(2L, "회의실 B", 5, BigDecimal.valueOf(7000));
    }

    @Test
    @DisplayName("모든 회의실 조회")
    void getAllMeetingRooms_shouldReturnAllMeetingRooms() {
        // Given
        List<MeetingRoom> expectedRooms = Arrays.asList(room1, room2);
        given(meetingRoomRepository.findAll()).willReturn(expectedRooms);

        // When
        List<MeetingRoomResponseDto> actualRooms = meetingRoomService.getAllMeetingRooms();

        // Then
        assertThat(actualRooms).hasSize(2);
        assertThat(actualRooms.get(0).getName()).isEqualTo("회의실 A");
        assertThat(actualRooms.get(1).getName()).isEqualTo("회의실 B");
    }

    @Test
    @DisplayName("회의실 생성")
    void createMeetingRoom_shouldReturnNewMeetingRoom() {
        // Given
        MeetingRoomRequestDto requestDto = new MeetingRoomRequestDto("새 회의실", 20, new BigDecimal("20000"));
        MeetingRoom newRoom = MeetingRoom.createForTest(3L, requestDto.getName(), requestDto.getCapacity(), requestDto.getPricePerHour());
        given(meetingRoomRepository.save(any(MeetingRoom.class))).willReturn(newRoom);

        // When
        MeetingRoomResponseDto responseDto = meetingRoomService.createMeetingRoom(requestDto);

        // Then
        verify(meetingRoomRepository).save(any(MeetingRoom.class));
        assertThat(responseDto.getName()).isEqualTo("새 회의실");
        assertThat(responseDto.getCapacity()).isEqualTo(20);
    }

    @Test
    @DisplayName("회의실 생성 실패 - 이미 존재하는 이름")
    void createMeetingRoom_Fail_AlreadyExistedName() {
        // Given
        MeetingRoomRequestDto requestDto = new MeetingRoomRequestDto("회의실 A", 10, new BigDecimal("10000"));
        given(meetingRoomRepository.existsByName("회의실 A")).willReturn(true);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingRoomService.createMeetingRoom(requestDto);
        });
        assertEquals(ErrorCode.MEETING_ROOM_ALREADY_EXISTED_NAME, exception.getErrorCode());
    }
}