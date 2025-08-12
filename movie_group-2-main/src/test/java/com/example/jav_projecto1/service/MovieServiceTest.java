package com.example.jav_projecto1.service;

import com.example.jav_projecto1.dto.MovieDTO;
import com.example.jav_projecto1.dto.MovieUpdateRequest;
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
 * Unit tests for MovieService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class MovieServiceTest {
    @Mock private MovieRepository movieRepository;

    @Mock private TypeRepository typeRepository;
    @Mock private MovieTypeRepository movieTypeRepository;
    @InjectMocks private MovieService movieService;

    private Movie movie;
    private MovieUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setMovieId("M1");
        movie.setMovieNameEnglish("Test Movie");
        movie.setFromDate(new Date(System.currentTimeMillis() - 100000));
        movie.setToDate(new Date(System.currentTimeMillis() + 100000));
        updateRequest = new MovieUpdateRequest();
        updateRequest.setMovieNameEnglish("Updated Movie");
        updateRequest.setActor("Actor");
        updateRequest.setDirector("Director");
        updateRequest.setContent("Content");
        updateRequest.setDuration(120);
        updateRequest.setFromDate(new Date());
        updateRequest.setToDate(new Date());
        updateRequest.setMovieProductionCompany("Company");
        updateRequest.setVersion("2D");
        updateRequest.setMovieNameVn("Phim");
        updateRequest.setLargeImage("img.jpg");
        updateRequest.setSmallImage("img_s.jpg");
        updateRequest.setTypes(List.of(1));
    }

    /**
     * Test getAllMovies should return list of MovieDTO.
     */
    @Test
    void testGetAllMovies() {
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        List<MovieDTO> result = movieService.getAllMovies();
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.getFirst().getMovieNameEnglish());
    }

    /**
     * Test addMovie should save and return the movie.
     */
    @Test
    void testAddMovie() {
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        Movie result = movieService.addMovie(movie);
        assertNotNull(result);
        assertEquals("M1", result.getMovieId());
    }

    /**
     * Test updateMovie should update and return MovieDTO if found.
     */
    @Test
    void testUpdateMovie_Found() {
        when(movieRepository.findById("M1")).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        ResponseEntity<MovieDTO> response = movieService.updateMovie("M1", updateRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Movie", response.getBody().getMovieNameEnglish());
    }

    /**
     * Test updateMovie should return 404 if not found.
     */
    @Test
    void testUpdateMovie_NotFound() {
        when(movieRepository.findById("M2")).thenReturn(Optional.empty());
        ResponseEntity<MovieDTO> response = movieService.updateMovie("M2", updateRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test deleteMovie should return no content if found.
     */
    @Test
    void testDeleteMovie_Found() {
        when(movieRepository.existsById("M1")).thenReturn(true);
        doNothing().when(movieRepository).deleteById("M1");
        ResponseEntity<Void> response = movieService.deleteMovie("M1");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    /**
     * Test deleteMovie should return not found if not exists.
     */
    @Test
    void testDeleteMovie_NotFound() {
        when(movieRepository.existsById("M2")).thenReturn(false);
        ResponseEntity<Void> response = movieService.deleteMovie("M2");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test searchMoviesByType should return movies with correct type.
     */
    @Test
    void testSearchMoviesByType() {
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        ResponseEntity<?> response = movieService.searchMoviesByType(null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.containsKey("Search"));
    }

    /**
     * Test getMovieDetail should return MovieDTO if found.
     */
    @Test
    void testGetMovieDetail_Found() {
        when(movieRepository.findById("M1")).thenReturn(Optional.of(movie));
        ResponseEntity<MovieDTO> response = movieService.getMovieDetail("M1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Movie", response.getBody().getMovieNameEnglish());
    }

    /**
     * Test getMovieDetail should return 404 if not found.
     */
    @Test
    void testGetMovieDetail_NotFound() {
        when(movieRepository.findById("M2")).thenReturn(Optional.empty());
        ResponseEntity<MovieDTO> response = movieService.getMovieDetail("M2");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test getShowtimesByDate should return search results.
     */
    @Test
    void testGetShowtimesByDate() {
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        ResponseEntity<?> response = movieService.getShowtimesByDate("2024-01-01");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.containsKey("Search"));
    }

    /**
     * Test getNowShowingMovies should return movies currently showing.
     */
    @Test
    void testGetNowShowingMovies() {
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        ResponseEntity<?> response = movieService.getNowShowingMovies();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.containsKey("Search"));
    }

    /**
     * Test getComingSoonMovies should return movies coming soon.
     */
    @Test
    void testGetComingSoonMovies() {
        Movie futureMovie = new Movie();
        futureMovie.setMovieId("M2");
        futureMovie.setMovieNameEnglish("Future Movie");
        futureMovie.setFromDate(new Date(System.currentTimeMillis() + 10000000));
        when(movieRepository.findAll()).thenReturn(List.of(futureMovie));
        ResponseEntity<?> response = movieService.getComingSoonMovies();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.containsKey("Search"));
    }
}