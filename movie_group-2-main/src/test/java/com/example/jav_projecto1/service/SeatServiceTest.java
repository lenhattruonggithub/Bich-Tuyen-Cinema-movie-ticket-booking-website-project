package com.example.jav_projecto1.service;

import com.example.jav_projecto1.entities.Seat;
import com.example.jav_projecto1.repository.SeatRepository;
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

/**
 * Unit tests for SeatService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class SeatServiceTest {
    @Mock
    private SeatRepository seatRepository;
    @InjectMocks
    private SeatService seatService;

    private Seat seat;

    @BeforeEach
    void setUp() {
        seat = new Seat();
        seat.setSeatId(1L);
        seat.setRowLabel("A");
        seat.setSeatNumber(1);
        seat.setSeatCode("A1");
        seat.setStatus(true);
    }

    /**
     * Test getAllSeats should return a list of seats.
     */
    @Test
    void testGetAllSeats() {
        when(seatRepository.findAll()).thenReturn(List.of(seat));
        List<Seat> result = seatService.getAllSeats();
        assertEquals(1, result.size());
        assertEquals("A", result.getFirst().getRowLabel());
    }

    /**
     * Test getSeatById should return seat if found.
     */
    @Test
    void testGetSeatById_Found() {
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        Optional<Seat> result = seatService.getSeatById(1L);
        assertTrue(result.isPresent());
        assertEquals("A", result.get().getRowLabel());
    }

    /**
     * Test getSeatById should return empty if not found.
     */
    @Test
    void testGetSeatById_NotFound() {
        when(seatRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Seat> result = seatService.getSeatById(2L);
        assertFalse(result.isPresent());
    }

    /**
     * Test createSeat should save and return the seat.
     */
    @Test
    void testCreateSeat() {
        when(seatRepository.save(any(Seat.class))).thenReturn(seat);
        Seat saved = seatService.createSeat(seat);
        assertNotNull(saved);
        assertEquals("A", saved.getRowLabel());
    }

    /**
     * Test updateSeat should update and return the seat if found.
     */
    @Test
    void testUpdateSeat_Found() {
        Seat updatedDetails = new Seat();
        updatedDetails.setRowLabel("B");
        updatedDetails.setSeatNumber(2);
        updatedDetails.setSeatCode("B2");
        updatedDetails.setStatus(false);
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenReturn(updatedDetails);
        Optional<Seat> result = seatService.updateSeat(1L, updatedDetails);
        assertTrue(result.isPresent());
        assertEquals("B", result.get().getRowLabel());
    }

    /**
     * Test updateSeat should return empty if seat not found.
     */
    @Test
    void testUpdateSeat_NotFound() {
        Seat updatedDetails = new Seat();
        updatedDetails.setRowLabel("B");
        when(seatRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Seat> result = seatService.updateSeat(2L, updatedDetails);
        assertFalse(result.isPresent());
    }

    /**
     * Test deleteSeat should return true if seat exists and is deleted.
     */
    @Test
    void testDeleteSeat_Success() {
        when(seatRepository.existsById(1L)).thenReturn(true);
        doNothing().when(seatRepository).deleteById(1L);
        boolean result = seatService.deleteSeat(1L);
        assertTrue(result);
    }

    /**
     * Test deleteSeat should return false if seat does not exist.
     */
    @Test
    void testDeleteSeat_NotFound() {
        when(seatRepository.existsById(2L)).thenReturn(false);
        boolean result = seatService.deleteSeat(2L);
        assertFalse(result);
    }

    /**
     * Test getSeatsByCinemaRoomId should return list of seats.
     */
    @Test
    void testGetSeatsByCinemaRoomId() {
        when(seatRepository.findByCinemaRoom_CinemaRoomId(1)).thenReturn(List.of(seat));
        List<Seat> result = seatService.getSeatsByCinemaRoomId(1);
        assertEquals(1, result.size());
    }

    /**
     * Test unbindSeatFromCinemaRoom should set cinemaRoom to null for all seats.
     */
    @Test
    void testUnbindSeatFromCinemaRoom() {
        Seat seat2 = new Seat();
        seat2.setSeatId(2L);
        seat2.setRowLabel("B");
        seat2.setSeatNumber(2);
        seat2.setSeatCode("B2");
        seat2.setStatus(true);
        when(seatRepository.findByCinemaRoom_CinemaRoomId(1)).thenReturn(List.of(seat, seat2));
        when(seatRepository.save(any(Seat.class))).thenReturn(seat, seat2);
        seatService.unbindSeatFromCinemaRoom(1);
        verify(seatRepository, times(2)).save(any(Seat.class));
    }
}