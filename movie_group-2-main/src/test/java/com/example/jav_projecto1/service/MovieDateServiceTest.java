package com.example.jav_projecto1.service;

import com.example.jav_projecto1.dto.MovieDateDTO;
import com.example.jav_projecto1.entities.MovieDate;
import com.example.jav_projecto1.entities.MovieDateId;
import com.example.jav_projecto1.repository.MovieDateRepository;
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
 * Unit tests for MovieDateService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class MovieDateServiceTest {
    @Mock private MovieDateRepository movieDateRepository;
    @InjectMocks private MovieDateService movieDateService;

    private MovieDate movieDate;
    private MovieDateId movieDateId;

    @BeforeEach
    void setUp() {
        movieDateId = new MovieDateId("M1", 1);
        movieDate = new MovieDate();
        movieDate.setId(movieDateId);
    }

    /**
     * Test getAll should return list of MovieDateDTO.
     */
    @Test
    void testGetAll() {
        when(movieDateRepository.findAll()).thenReturn(List.of(movieDate));
        List<MovieDateDTO> result = movieDateService.getAll();
        assertEquals(1, result.size());
        assertEquals("M1", result.getFirst().getMovieId());
    }

    /**
     * Test getById should return MovieDateDTO if found.
     */
    @Test
    void testGetById_Found() {
        when(movieDateRepository.findById(movieDateId)).thenReturn(Optional.of(movieDate));
        ResponseEntity<MovieDateDTO> response = movieDateService.getById("M1", 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("M1", response.getBody().getMovieId());
    }

    /**
     * Test getById should return 404 if not found.
     */
    @Test
    void testGetById_NotFound() {
        when(movieDateRepository.findById(movieDateId)).thenReturn(Optional.empty());
        ResponseEntity<MovieDateDTO> response = movieDateService.getById("M1", 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test create should return 201 and MovieDate if input is valid.
     */
    @Test
    void testCreate_Success() {
        when(movieDateRepository.save(any(MovieDate.class))).thenReturn(movieDate);
        ResponseEntity<?> response = movieDateService.create(movieDate);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("data"));
    }

    /**
     * Test create should return 400 if input is null or id is null.
     */
    @Test
    void testCreate_BadRequest() {
        ResponseEntity<?> response1 = movieDateService.create(null);
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        MovieDate invalid = new MovieDate();
        ResponseEntity<?> response2 = movieDateService.create(invalid);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    /**
     * Test update should return 200 and MovieDate if found.
     */
    @Test
    void testUpdate_Found() {
        when(movieDateRepository.existsById(movieDateId)).thenReturn(true);
        when(movieDateRepository.save(any(MovieDate.class))).thenReturn(movieDate);
        ResponseEntity<?> response = movieDateService.update("M1", 1, movieDate);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("data"));
    }

    /**
     * Test update should return 404 if not found.
     */
    @Test
    void testUpdate_NotFound() {
        when(movieDateRepository.existsById(movieDateId)).thenReturn(false);
        ResponseEntity<?> response = movieDateService.update("M1", 1, movieDate);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test delete should return 200 if found.
     */
    @Test
    void testDelete_Found() {
        when(movieDateRepository.existsById(movieDateId)).thenReturn(true);
        doNothing().when(movieDateRepository).deleteById(movieDateId);
        ResponseEntity<?> response = movieDateService.delete("M1", 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test delete should return 404 if not found.
     */
    @Test
    void testDelete_NotFound() {
        when(movieDateRepository.existsById(movieDateId)).thenReturn(false);
        ResponseEntity<?> response = movieDateService.delete("M1", 1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
} 