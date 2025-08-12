package com.example.jav_projecto1.service;

import com.example.jav_projecto1.dto.ScheduleDTO;
import com.example.jav_projecto1.entities.Schedule;
import com.example.jav_projecto1.entities.MovieDate;
import com.example.jav_projecto1.entities.MovieDateId;
import com.example.jav_projecto1.entities.MovieSchedule;
import com.example.jav_projecto1.entities.Movie;
import com.example.jav_projecto1.repository.MovieDateRepository;
import com.example.jav_projecto1.repository.MovieScheduleRepository;
import com.example.jav_projecto1.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ScheduleService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {
    @Mock private ScheduleRepository scheduleRepository;
    @Mock private MovieDateRepository movieDateRepository;
    @Mock private MovieScheduleRepository movieScheduleRepository;
    @InjectMocks private ScheduleService scheduleService;

    private Schedule schedule;

    @BeforeEach
    void setUp() {
        schedule = new Schedule();
        schedule.setScheduleId(1);
        schedule.setScheduleTime("10:00");
    }

    /**
     * Test getAll should return list of ScheduleDTO.
     */
    @Test
    void testGetAll() {
        when(scheduleRepository.findAll()).thenReturn(List.of(schedule));
        List<ScheduleDTO> result = scheduleService.getAll();
        assertEquals(1, result.size());
        assertEquals(1, result.getFirst().getScheduleId());
    }

    /**
     * Test getById should return ScheduleDTO if found.
     */
    @Test
    void testGetById_Found() {
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        ResponseEntity<ScheduleDTO> response = scheduleService.getById(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getScheduleId());
    }

    /**
     * Test getById should return 404 if not found.
     */
    @Test
    void testGetById_NotFound() {
        when(scheduleRepository.findById(2)).thenReturn(Optional.empty());
        ResponseEntity<ScheduleDTO> response = scheduleService.getById(2);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test create should return 201 and ScheduleDTO if input is valid.
     */
    @Test
    void testCreate_Success() {
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);
        ResponseEntity<?> response = scheduleService.create(schedule);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("data"));
    }

    /**
     * Test create should return 400 if input is null.
     */
    @Test
    void testCreate_BadRequest() {
        ResponseEntity<?> response = scheduleService.create(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test update should return 200 and ScheduleDTO if found.
     */
    @Test
    void testUpdate_Found() {
        when(scheduleRepository.existsById(1)).thenReturn(true);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);
        ResponseEntity<?> response = scheduleService.update(1, schedule);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("data"));
    }

    /**
     * Test update should return 404 if not found.
     */
    @Test
    void testUpdate_NotFound() {
        when(scheduleRepository.existsById(2)).thenReturn(false);
        ResponseEntity<?> response = scheduleService.update(2, schedule);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test delete should return 200 if found.
     */
    @Test
    void testDelete_Found() {
        when(scheduleRepository.existsById(1)).thenReturn(true);
        doNothing().when(scheduleRepository).deleteById(1);
        ResponseEntity<?> response = scheduleService.delete(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test delete should return 404 if not found.
     */
    @Test
    void testDelete_NotFound() {
        when(scheduleRepository.existsById(2)).thenReturn(false);
        ResponseEntity<?> response = scheduleService.delete(2);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test getSchedulesByMovieAndDate should return schedules if movie-date exists.
     */
    @Test
    void testGetSchedulesByMovieAndDate_Found() {
        MovieDateId movieDateId = new MovieDateId("M1", 1);
        MovieDate movieDate = new MovieDate();
        movieDate.setId(movieDateId);
        when(movieDateRepository.findById(movieDateId)).thenReturn(Optional.of(movieDate));
        MovieSchedule ms = new MovieSchedule();
        ms.setSchedule(schedule);
        ms.setMovie(new Movie());
        ms.getMovie().setMovieId("M1");
        when(movieScheduleRepository.findAll()).thenReturn(List.of(ms));
        ResponseEntity<?> response = scheduleService.getSchedulesByMovieAndDate("M1", 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<?> body = (List<?>) response.getBody();
        assertNotNull(body);
    }

    /**
     * Test getSchedulesByMovieAndDate should return bad request if movie-date not found.
     */
    @Test
    void testGetSchedulesByMovieAndDate_NotFound() {
        MovieDateId movieDateId = new MovieDateId("M1", 1);
        when(movieDateRepository.findById(movieDateId)).thenReturn(Optional.empty());
        ResponseEntity<?> response = scheduleService.getSchedulesByMovieAndDate("M1", 1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
} 