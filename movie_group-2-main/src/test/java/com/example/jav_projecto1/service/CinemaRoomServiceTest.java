package com.example.jav_projecto1.service;

import com.example.jav_projecto1.entities.CinemaRoom;
import com.example.jav_projecto1.repository.CinemaRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CinemaRoomServiceTest {
    @Mock private CinemaRoomRepository cinemaRoomRepository;
    @Mock private SeatService seatService;
    @InjectMocks private CinemaRoomService cinemaRoomService;

    private CinemaRoom room;

    @BeforeEach
    void setUp() {
        room = new CinemaRoom();
        room.setCinemaRoomId(1);
        room.setRoomName("Room 1");
        room.setCapacity(100);
        room.setDescription("Test room");
    }

    /**
     * Test getAllCinemaRooms should return list of rooms.
     */
    @Test
    void testGetAllCinemaRooms() {
        when(cinemaRoomRepository.findAll()).thenReturn(List.of(room));
        List<CinemaRoom> result = cinemaRoomService.getAllCinemaRooms();
        assertEquals(1, result.size());
        assertEquals("Room 1", result.getFirst().getRoomName());
    }

    /**
     * Test getCinemaRoomById should return room if found.
     */
    @Test
    void testGetCinemaRoomById_Found() {
        when(cinemaRoomRepository.findById(1)).thenReturn(Optional.of(room));
        Optional<CinemaRoom> result = cinemaRoomService.getCinemaRoomById(1);
        assertTrue(result.isPresent());
        assertEquals("Room 1", result.get().getRoomName());
    }

    /**
     * Test getCinemaRoomById should return empty if not found.
     */
    @Test
    void testGetCinemaRoomById_NotFound() {
        when(cinemaRoomRepository.findById(2)).thenReturn(Optional.empty());
        Optional<CinemaRoom> result = cinemaRoomService.getCinemaRoomById(2);
        assertFalse(result.isPresent());
    }

    /**
     * Test createCinemaRoom should save and return the room.
     */
    @Test
    void testCreateCinemaRoom() {
        when(cinemaRoomRepository.save(any(CinemaRoom.class))).thenReturn(room);
        CinemaRoom saved = cinemaRoomService.createCinemaRoom(room);
        assertNotNull(saved);
        assertEquals("Room 1", saved.getRoomName());
    }

    /**
     * Test updateCinemaRoom should update and return the room if found.
     */
    @Test
    void testUpdateCinemaRoom_Found() {
        CinemaRoom updatedDetails = new CinemaRoom();
        updatedDetails.setRoomName("Room 2");
        updatedDetails.setCapacity(200);
        updatedDetails.setDescription("Updated");
        when(cinemaRoomRepository.findById(1)).thenReturn(Optional.of(room));
        when(cinemaRoomRepository.save(any(CinemaRoom.class))).thenReturn(updatedDetails);
        Optional<CinemaRoom> result = cinemaRoomService.updateCinemaRoom(1, updatedDetails);
        assertTrue(result.isPresent());
        assertEquals("Room 2", result.get().getRoomName());
    }

    /**
     * Test updateCinemaRoom should return empty if not found.
     */
    @Test
    void testUpdateCinemaRoom_NotFound() {
        CinemaRoom updatedDetails = new CinemaRoom();
        updatedDetails.setRoomName("Room 2");
        when(cinemaRoomRepository.findById(2)).thenReturn(Optional.empty());
        Optional<CinemaRoom> result = cinemaRoomService.updateCinemaRoom(2, updatedDetails);
        assertFalse(result.isPresent());
    }

    /**
     * Test deleteCinemaRoom should return true if room exists and is deleted.
     */
    @Test
    void testDeleteCinemaRoom_Success() {
        when(cinemaRoomRepository.existsById(1)).thenReturn(true);
        doNothing().when(seatService).unbindSeatFromCinemaRoom(1);
        doNothing().when(cinemaRoomRepository).deleteById(1);
        boolean result = cinemaRoomService.deleteCinemaRoom(1);
        assertTrue(result);
        verify(seatService).unbindSeatFromCinemaRoom(1);
        verify(cinemaRoomRepository).deleteById(1);
    }

    /**
     * Test deleteCinemaRoom should return false if room does not exist.
     */
    @Test
    void testDeleteCinemaRoom_NotFound() {
        when(cinemaRoomRepository.existsById(2)).thenReturn(false);
        boolean result = cinemaRoomService.deleteCinemaRoom(2);
        assertFalse(result);
    }
} 