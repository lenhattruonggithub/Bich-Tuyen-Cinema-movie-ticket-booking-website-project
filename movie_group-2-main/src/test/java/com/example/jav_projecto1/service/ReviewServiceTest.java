package com.example.jav_projecto1.service;

import com.example.jav_projecto1.dto.ReviewRequest;
import com.example.jav_projecto1.entities.*;
import com.example.jav_projecto1.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReviewService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock private ReviewRepository reviewRepository;
    @Mock private MovieRepository movieRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private InvoiceRepository invoiceRepository;
    @InjectMocks private ReviewService reviewService;

    private ReviewRequest reviewRequest;
    private Movie movie;
    private Account account;
    private Review review;

    @BeforeEach
    void setUp() {
        reviewRequest = new ReviewRequest();
        reviewRequest.setMovieId("M1");
        reviewRequest.setAccountId(1L);
        reviewRequest.setRating(5);
        reviewRequest.setComment("Great!");
        movie = new Movie();
        movie.setMovieId("M1");
        movie.setMovieNameEnglish("Test Movie");
        account = new Account();
        account.setAccountId(1L);
        account.setName("User");
        review = Review.builder().reviewId(1L).movie(movie).account(account).rating(5).comment("Great!").createdAt(new java.sql.Timestamp(System.currentTimeMillis())).build();
    }

    /**
     * Test submitReview should return ok if all conditions are met.
     */
    @Test
    void testSubmitReview_Success() {
        when(movieRepository.findById("M1")).thenReturn(Optional.of(movie));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(invoiceRepository.hasUserBoughtTicketForMovie(1L, "Test Movie")).thenReturn(true);
        when(reviewRepository.findByMovie_MovieId("M1")).thenReturn(List.of());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        ResponseEntity<?> response = reviewService.submitReview(reviewRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Review submitted", response.getBody());
    }

    /**
     * Test submitReview should return bad request if movie or account not found.
     */
    @Test
    void testSubmitReview_MovieOrAccountNotFound() {
        when(movieRepository.findById("M1")).thenReturn(Optional.empty());
        ResponseEntity<?> response = reviewService.submitReview(reviewRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        when(movieRepository.findById("M1")).thenReturn(Optional.of(movie));
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        response = reviewService.submitReview(reviewRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test submitReview should return bad request if user has not bought ticket.
     */
    @Test
    void testSubmitReview_NotBoughtTicket() {
        when(movieRepository.findById("M1")).thenReturn(Optional.of(movie));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(invoiceRepository.hasUserBoughtTicketForMovie(1L, "Test Movie")).thenReturn(false);
        ResponseEntity<?> response = reviewService.submitReview(reviewRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test submitReview should return bad request if user already reviewed.
     */
    @Test
    void testSubmitReview_AlreadyReviewed() {
        when(movieRepository.findById("M1")).thenReturn(Optional.of(movie));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(invoiceRepository.hasUserBoughtTicketForMovie(1L, "Test Movie")).thenReturn(true);
        when(reviewRepository.findByMovie_MovieId("M1")).thenReturn(List.of(review));
        ResponseEntity<?> response = reviewService.submitReview(reviewRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test getAverageRating should return correct average and total reviews.
     */
    @Test
    void testGetAverageRating() {
        when(reviewRepository.findAverageRatingByMovieId("M1")).thenReturn(4.5);
        when(reviewRepository.findByMovie_MovieId("M1")).thenReturn(List.of(review));
        ResponseEntity<?> response = reviewService.getAverageRating("M1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(4.5, body.get("averageRating"));
        assertEquals(1, body.get("totalReviews"));
    }

    /**
     * Test getReviewsByMovie should return list of ReviewResponse.
     */
    @Test
    void testGetReviewsByMovie() {
        when(reviewRepository.findByMovie_MovieId("M1")).thenReturn(List.of(review));
        ResponseEntity<?> response = reviewService.getReviewsByMovie("M1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<?> body = (List<?>) response.getBody();
        assertEquals(1, body.size());
    }

    /**
     * Test updateReview should update and return ok if found.
     */
    @Test
    void testUpdateReview_Found() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        ResponseEntity<?> response = reviewService.updateReview(1L, reviewRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Review updated", response.getBody());
    }

    /**
     * Test updateReview should return not found if review does not exist.
     */
    @Test
    void testUpdateReview_NotFound() {
        when(reviewRepository.findById(2L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = reviewService.updateReview(2L, reviewRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test deleteReview should delete and return ok if found.
     */
    @Test
    void testDeleteReview_Found() {
        when(reviewRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1L);
        ResponseEntity<?> response = reviewService.deleteReview(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Review deleted", response.getBody());
    }

    /**
     * Test deleteReview should return not found if review does not exist.
     */
    @Test
    void testDeleteReview_NotFound() {
        when(reviewRepository.existsById(2L)).thenReturn(false);
        ResponseEntity<?> response = reviewService.deleteReview(2L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
} 