package com.example.jav_projecto1.service;

import com.example.jav_projecto1.dto.MovieDTO;
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
 * Unit tests for BookingService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock private MovieRepository movieRepository;
    @Mock private ScheduleRepository scheduleRepository;
    @Mock private InvoiceRepository invoiceRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private RewardPointRepository rewardPointRepository;
    @Mock private EmailService emailService;
    @InjectMocks private BookingService bookingService;

    private Movie movie;
    private Schedule schedule;
    private Invoice invoice;
    private Account account;
    private Member member;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setMovieId("M1");
        movie.setMovieNameEnglish("Test Movie");
        schedule = new Schedule();
        schedule.setScheduleId(1);
        schedule.setScheduleTime("10:00");
        account = new Account();
        account.setAccountId(1L);
        account.setEmail("test@example.com");
        member = Member.builder().account(account).score(0).build();
        invoice = new Invoice();
        invoice.setInvoiceId(1L);
        invoice.setMovieName("Test Movie");
        invoice.setScheduleShow("2024-01-01");
        invoice.setScheduleShowTime("10:00");
        invoice.setSeat("A1");
        invoice.setStatus(true);
        invoice.setTotalMoney(100000);
        invoice.setBookingDate(new java.sql.Timestamp(System.currentTimeMillis()));
        invoice.setAccount(account);
    }

    /**
     * Test getAllMovies should return list of MovieDTO.
     */
    @Test
    void testGetAllMovies() {
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        List<MovieDTO> result = bookingService.getAllMovies();
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.getFirst().getMovieNameEnglish());
    }

    /**
     * Test getAllSchedules should return list of schedules.
     */
    @Test
    void testGetAllSchedules() {
        when(scheduleRepository.findAll()).thenReturn(List.of(schedule));
        List<Schedule> result = bookingService.getAllSchedules();
        assertEquals(1, result.size());
        assertEquals(1, result.getFirst().getScheduleId());
    }

    /**
     * Test getBookedSeats should return list of booked seat codes.
     */
    @Test
    void testGetBookedSeats() {
        when(invoiceRepository.findBookedSeats(anyString(), anyString())).thenReturn(List.of("A1", "A2"));
        List<String> result = bookingService.getBookedSeats("Test Movie", "2024-01-01");
        assertEquals(2, result.size());
        assertTrue(result.contains("A1"));
    }

    /**
     * Test confirmBooking should return ok if account exists and booking is new.
     */
    @Test
    void testConfirmBooking_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(invoiceRepository.findAll()).thenReturn(List.of());
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(memberRepository.findByAccount_AccountId(1L)).thenReturn(Optional.of(member));
        when(rewardPointRepository.save(any(RewardPoint.class))).thenReturn(new RewardPoint());
        ResponseEntity<?> response = bookingService.confirmBooking(invoice, 1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(emailService).sendBookingConfirmation(anyString(), anyString(), anyString(), anyString());
    }

    /**
     * Test confirmBooking should return bad request if account not found.
     */
    @Test
    void testConfirmBooking_AccountNotFound() {
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = bookingService.confirmBooking(invoice, 2L);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test handleConfirmBooking should delegate to confirmBooking.
     */
    @Test
    void testHandleConfirmBooking() {
        Map<String, Object> invoiceData = Map.of(
                "movieName", "Test Movie",
                "scheduleShow", "2024-01-01",
                "scheduleShowTime", "10:00",
                "seat", "A1",
                "status", true,
                "totalMoney", 100000
        );
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(invoiceRepository.findAll()).thenReturn(List.of());
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(memberRepository.findByAccount_AccountId(1L)).thenReturn(Optional.of(member));
        when(rewardPointRepository.save(any(RewardPoint.class))).thenReturn(new RewardPoint());
        ResponseEntity<?> response = bookingService.handleConfirmBooking(invoiceData, 1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test getBookingHistory should return list of InvoiceDTO for account.
     */
    @Test
    void testGetBookingHistory() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        invoice.setAccount(account);
        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));
        ResponseEntity<?> response = bookingService.getBookingHistory(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = (List<?>) response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
    }

    /**
     * Test getBookingHistory should return bad request if account not found.
     */
    @Test
    void testGetBookingHistory_AccountNotFound() {
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = bookingService.getBookingHistory(2L);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test getTicketInfo should return ticket info if invoice exists.
     */
    @Test
    void testGetTicketInfo_Found() {
        // Setup movie with version and cinemaRoom to avoid null values in Map.of()
        CinemaRoom cinemaRoom = new CinemaRoom();
        cinemaRoom.setCinemaRoomId(1);
        cinemaRoom.setRoomName("Room 1");
        movie.setVersion("2D");
        movie.setCinemaRoom(cinemaRoom);

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        ResponseEntity<?> response = bookingService.getTicketInfo(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Test Movie", body.get("movieName"));
    }

    /**
     * Test getTicketInfo should return not found if invoice does not exist.
     */
    @Test
    void testGetTicketInfo_NotFound() {
        when(invoiceRepository.findById(2L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = bookingService.getTicketInfo(2L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test getSchedulesByMovieId should return schedules if movie exists.
     */
    @Test
    void testGetSchedulesByMovieId_Found() {
        MovieSchedule ms = new MovieSchedule();
        ms.setSchedule(schedule);
        movie.setMovieSchedules(List.of(ms));
        when(movieRepository.findById("M1")).thenReturn(Optional.of(movie));
        ResponseEntity<List<Schedule>> response = bookingService.getSchedulesByMovieId("M1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    /**
     * Test getSchedulesByMovieId should return not found if movie does not exist.
     */
    @Test
    void testGetSchedulesByMovieId_NotFound() {
        when(movieRepository.findById("M2")).thenReturn(Optional.empty());
        ResponseEntity<List<Schedule>> response = bookingService.getSchedulesByMovieId("M2");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test cancelBooking should return ok if invoice exists and is paid.
     */
    @Test
    void testCancelBooking_Success() {
        invoice.setStatus(true);
        invoice.setAccount(account);
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(memberRepository.findByAccount_AccountId(1L)).thenReturn(Optional.of(member));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(rewardPointRepository.save(any(RewardPoint.class))).thenReturn(new RewardPoint());
        ResponseEntity<?> response = bookingService.cancelBooking(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(emailService).sendCancelBooking(anyString(), anyString(), anyString(), anyString());
    }

    /**
     * Test cancelBooking should return bad request if invoice does not exist.
     */
    @Test
    void testCancelBooking_InvoiceNotFound() {
        when(invoiceRepository.findById(2L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = bookingService.cancelBooking(2L);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test cancelBooking should return bad request if invoice is not paid.
     */
    @Test
    void testCancelBooking_InvoiceNotPaid() {
        invoice.setStatus(false);
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        ResponseEntity<?> response = bookingService.cancelBooking(1L);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}