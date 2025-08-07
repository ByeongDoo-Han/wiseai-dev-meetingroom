package com.example.meetingroom.service;

import com.example.meetingroom.entity.MeetingRoom;
import com.example.meetingroom.repository.MeetingRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetingRoomServiceTest {

    @Mock
    private MeetingRoomRepository meetingRoomRepository;

    @InjectMocks
    private MeetingRoomService meetingRoomService;

    private MeetingRoom room1;
    private MeetingRoom room2;

    @BeforeEach
    void setUp() {
        room1 = MeetingRoom.builder()
            .id(1L)
            .name("회의실 A")
            .capacity(10)
            .pricePerHour(new BigDecimal("10000"))
            .build();

        room2 = MeetingRoom.builder()
            .id(2L)
            .name("회의실 B")
            .capacity(5)
            .pricePerHour(new BigDecimal("7000"))
            .build();
    }

    @Test
    @DisplayName("모든 회의실 조회")
    void getAllMeetingRooms_shouldReturnAllMeetingRooms() {
        // Given
        List<MeetingRoom> expectedRooms = Arrays.asList(room1, room2);
        when(meetingRoomRepository.findAll()).thenReturn(expectedRooms);

        // When
        List<MeetingRoom> actualRooms = meetingRoomService.getAllMeetingRooms();

        // Then
        assertEquals(expectedRooms.size(), actualRooms.size());
        assertEquals(expectedRooms.get(0).getName(), actualRooms.get(0).getName());
        assertEquals(expectedRooms.get(1).getName(), actualRooms.get(1).getName());
    }
}
