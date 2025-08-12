package com.example.jav_projecto1.service;

import com.example.jav_projecto1.entities.Invoice;
import com.example.jav_projecto1.entities.Movie;
import com.example.jav_projecto1.repository.CinemaRoomRepository;
import com.example.jav_projecto1.repository.InvoiceRepository;
import com.example.jav_projecto1.repository.MovieRepository;
import com.example.jav_projecto1.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReportService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock private InvoiceRepository invoiceRepository;
    @Mock private MovieRepository movieRepository;
    @Mock private CinemaRoomRepository cinemaRoomRepository;
    @Mock private ReviewRepository reviewRepository;
    @InjectMocks private ReportService reportService;

    private Invoice invoice;
    private Movie movie;

    @BeforeEach
    void setUp() {
        invoice = new Invoice();
        invoice.setTotalMoney(100000);
        invoice.setMovieName("Test Movie");
        invoice.setBookingDate(new java.sql.Timestamp(System.currentTimeMillis()));
        movie = new Movie();
        movie.setMovieId("M1");
        movie.setMovieNameEnglish("Test Movie");
        movie.setFromDate(new Date(System.currentTimeMillis() - 100000));
        movie.setToDate(new Date(System.currentTimeMillis() + 100000));
    }

    /**
     * Test getOverview should return correct stats, revenueData, and topMovies.
     */
    @Test
    void testGetOverview() {
        when(invoiceRepository.count()).thenReturn(10L);
        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(cinemaRoomRepository.count()).thenReturn(5L);
        when(reviewRepository.findAverageRatingByMovieId(anyString())).thenReturn(4.5);
        when(reviewRepository.findByMovie_MovieId(anyString())).thenReturn(List.of());

        Map<String, Object> result = reportService.getOverview();
        assertNotNull(result);
        assertTrue(result.containsKey("stats"));
        assertTrue(result.containsKey("revenueData"));
        assertTrue(result.containsKey("topMovies"));
        List<?> stats = (List<?>) result.get("stats");
        assertFalse(stats.isEmpty());
        List<?> revenueData = (List<?>) result.get("revenueData");
        assertEquals(12, revenueData.size());
        List<?> topMovies = (List<?>) result.get("topMovies");
        assertFalse(topMovies.isEmpty());
    }
} 