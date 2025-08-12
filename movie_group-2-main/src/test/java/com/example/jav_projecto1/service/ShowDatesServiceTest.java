package com.example.jav_projecto1.service;

import com.example.jav_projecto1.dto.ShowDatesDTO;
import com.example.jav_projecto1.entities.ShowDates;
import com.example.jav_projecto1.repository.ShowDatesRepository;
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
 * Unit tests for ShowDatesService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class ShowDatesServiceTest {
    @Mock
    private ShowDatesRepository showDatesRepository;
    @InjectMocks
    private ShowDatesService showDatesService;

    private ShowDates showDates;

    @BeforeEach
    void setUp() {
        showDates = new ShowDates();
        showDates.setShowDateId(1);
        showDates.setDateName("Today");
        showDates.setShowDate(new java.util.Date());
    }

    /**
     * Test getAll should return a list of ShowDatesDTO.
     */
    @Test
    void testGetAll() {
        when(showDatesRepository.findAll()).thenReturn(List.of(showDates));
        List<ShowDatesDTO> result = showDatesService.getAll();
        assertEquals(1, result.size());
        assertEquals("Today", result.getFirst().getDateName());
    }

    /**
     * Test getById should return ShowDatesDTO if found.
     */
    @Test
    void testGetById_Found() {
        when(showDatesRepository.findById(1)).thenReturn(Optional.of(showDates));
        ResponseEntity<ShowDatesDTO> response = showDatesService.getById(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Today", response.getBody().getDateName());
    }

    /**
     * Test getById should return 404 if not found.
     */
    @Test
    void testGetById_NotFound() {
        when(showDatesRepository.findById(2)).thenReturn(Optional.empty());
        ResponseEntity<ShowDatesDTO> response = showDatesService.getById(2);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test create should return 201 and ShowDatesDTO if input is valid.
     */
    @Test
    void testCreate_Success() {
        when(showDatesRepository.save(any(ShowDates.class))).thenReturn(showDates);
        ResponseEntity<?> response = showDatesService.create(showDates);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("data"));
    }

    /**
     * Test create should return 400 if input is null.
     */
    @Test
    void testCreate_BadRequest() {
        ResponseEntity<?> response = showDatesService.create(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test update should return 200 and ShowDatesDTO if found.
     */
    @Test
    void testUpdate_Found() {
        when(showDatesRepository.existsById(1)).thenReturn(true);
        when(showDatesRepository.save(any(ShowDates.class))).thenReturn(showDates);
        ResponseEntity<?> response = showDatesService.update(1, showDates);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("data"));
    }

    /**
     * Test update should return 404 if not found.
     */
    @Test
    void testUpdate_NotFound() {
        when(showDatesRepository.existsById(2)).thenReturn(false);
        ResponseEntity<?> response = showDatesService.update(2, showDates);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test delete should return 200 if found.
     */
    @Test
    void testDelete_Found() {
        when(showDatesRepository.existsById(1)).thenReturn(true);
        doNothing().when(showDatesRepository).deleteById(1);
        ResponseEntity<?> response = showDatesService.delete(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test delete should return 404 if not found.
     */
    @Test
    void testDelete_NotFound() {
        when(showDatesRepository.existsById(2)).thenReturn(false);
        ResponseEntity<?> response = showDatesService.delete(2);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
