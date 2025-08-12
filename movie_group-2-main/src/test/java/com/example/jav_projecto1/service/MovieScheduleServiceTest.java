package com.example.jav_projecto1.service;

import com.example.jav_projecto1.dto.MovieScheduleDTO;
import com.example.jav_projecto1.entities.MovieSchedule;
import com.example.jav_projecto1.entities.MovieScheduleId;
import com.example.jav_projecto1.repository.MovieScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MovieScheduleService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class MovieScheduleServiceTest {
    @Mock private MovieScheduleRepository movieScheduleRepository;
    @InjectMocks private MovieScheduleService movieScheduleService;

    private MovieSchedule movieSchedule;
    private MovieScheduleId movieScheduleId;

    @BeforeEach
    void setUp() {
        movieScheduleId = new MovieScheduleId("M1", 1);
        movieSchedule = new MovieSchedule();
        movieSchedule.setId(movieScheduleId);
    }

    /**
     * Test getAll should return list of MovieScheduleDTO.
     */
    @Test
    void testGetAll() {
        when(movieScheduleRepository.findAll()).thenReturn(List.of(movieSchedule));
        List<MovieScheduleDTO> result = movieScheduleService.getAll();
        assertEquals(1, result.size());
        assertEquals("M1", result.getFirst().getMovieId());
    }

    /**
     * Test getById should return MovieScheduleDTO if found.
     */
    @Test
    void testGetById_Found() {
        when(movieScheduleRepository.findById(movieScheduleId)).thenReturn(Optional.of(movieSchedule));
        ResponseEntity<MovieScheduleDTO> response = movieScheduleService.getById("M1", 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("M1", response.getBody().getMovieId());
    }

    /**
     * Test getById should return 404 if not found.
     */
    @Test
    void testGetById_NotFound() {
        when(movieScheduleRepository.findById(movieScheduleId)).thenReturn(Optional.empty());
        ResponseEntity<MovieScheduleDTO> response = movieScheduleService.getById("M1", 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test create should return 201 and MovieScheduleDTO if input is valid.
     */
    @Test
    void testCreate_Success() {
        when(movieScheduleRepository.save(any(MovieSchedule.class))).thenReturn(movieSchedule);
        ResponseEntity<?> response = movieScheduleService.create(movieSchedule);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("data"));
    }

    /**
     * Test create should return 400 if input is null or id is null.
     */
    @Test
    void testCreate_BadRequest() {
        ResponseEntity<?> response1 = movieScheduleService.create(null);
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        MovieSchedule invalid = new MovieSchedule();
        ResponseEntity<?> response2 = movieScheduleService.create(invalid);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    /**
     * Test update should return 200 and MovieScheduleDTO if found.
     */
    @Test
    void testUpdate_Found() {
        when(movieScheduleRepository.existsById(movieScheduleId)).thenReturn(true);
        when(movieScheduleRepository.save(any(MovieSchedule.class))).thenReturn(movieSchedule);
        ResponseEntity<?> response = movieScheduleService.update("M1", 1, movieSchedule);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("data"));
    }

    /**
     * Test update should return 404 if not found.
     */
    @Test
    void testUpdate_NotFound() {
        when(movieScheduleRepository.existsById(movieScheduleId)).thenReturn(false);
        ResponseEntity<?> response = movieScheduleService.update("M1", 1, movieSchedule);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test delete should return 200 if found.
     */
    @Test
    void testDelete_Found() {
        when(movieScheduleRepository.existsById(movieScheduleId)).thenReturn(true);
        doNothing().when(movieScheduleRepository).deleteById(movieScheduleId);
        ResponseEntity<?> response = movieScheduleService.delete("M1", 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test delete should return 404 if not found.
     */
    @Test
    void testDelete_NotFound() {
        when(movieScheduleRepository.existsById(movieScheduleId)).thenReturn(false);
        ResponseEntity<?> response = movieScheduleService.delete("M1", 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
} 