package ru.yandex.practicum.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.booking.dto.BookingAddDto;
import ru.yandex.practicum.booking.dto.BookingDto;
import ru.yandex.practicum.booking.service.BookingService;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private final Long userId = 1L;
    private final Long bookingId = 1L;
    private final String requestHeader = "X-Sharer-User-Id";

    private final BookingDto testBookingDto = BookingDto.builder()
            .id(bookingId)
            .build();

    private final BookingAddDto testBookingAddDto = BookingAddDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1).toString())
            .end(LocalDateTime.now().plusDays(2).toString())
            .build();

    @Test
    void getAllUserBookings_shouldReturnUserBookings() throws Exception {
        // Given
        List<BookingDto> bookings = List.of(testBookingDto);
        when(bookingService.getAllBookingsByUserId(userId)).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/bookings/owner")
                        .header(requestHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId));

        verify(bookingService).getAllBookingsByUserId(userId);
    }

    @Test
    void getAllUserBookings_withoutUserIdHeader_shouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/bookings/owner"))
                .andExpect(status().is5xxServerError());

        verify(bookingService, never()).getAllBookingsByUserId(anyLong());
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        // Given
        when(bookingService.getBookingById(bookingId, userId)).thenReturn(testBookingDto);

        // When & Then
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(requestHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));

        verify(bookingService).getBookingById(bookingId, userId);
    }

    @Test
    void getBookingById_withNegativeBookingId_shouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/bookings/{bookingId}", -1L)
                        .header(requestHeader, userId))
                .andExpect(status().is5xxServerError());

        verify(bookingService, never()).getBookingById(anyLong(), anyLong());
    }

    @Test
    void getBookingById_withZeroBookingId_shouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/bookings/{bookingId}", 0L)
                        .header(requestHeader, userId))
                .andExpect(status().is5xxServerError());

        verify(bookingService, never()).getBookingById(anyLong(), anyLong());
    }

    @Test
    void getBookingById_whenBookingNotFound_shouldReturnNotFound() throws Exception {
        // Given
        when(bookingService.getBookingById(bookingId, userId))
                .thenThrow(new NotFoundException("Booking not found"));

        // When & Then
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(requestHeader, userId))
                .andExpect(status().isNotFound());

        verify(bookingService).getBookingById(bookingId, userId);
    }

    @Test
    void createNewBooking_shouldCreateBooking() throws Exception {
        // Given
        when(bookingService.createBooking(any(BookingAddDto.class), eq(userId)))
                .thenReturn(testBookingDto);

        // When & Then
        mockMvc.perform(post("/bookings")
                        .header(requestHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookingAddDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));

        verify(bookingService).createBooking(any(BookingAddDto.class), eq(userId));
    }

    @Test
    void createNewBooking_withInvalidData_shouldReturnBadRequest() throws Exception {
        // Given - booking без itemId (обязательное поле)
        BookingAddDto invalidDto = BookingAddDto.builder()
                .start(LocalDateTime.now().plusDays(1).toString())
                .end(LocalDateTime.now().plusDays(2).toString())
                .build();

        // When & Then
        mockMvc.perform(post("/bookings")
                        .header(requestHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().is5xxServerError());

        verify(bookingService, never()).createBooking(any(), anyLong());
    }

    @Test
    void createNewBooking_withoutUserIdHeader_shouldReturnBadRequest() throws Exception {
        // Given
        BookingAddDto validDto = BookingAddDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1).toString())
                .end(LocalDateTime.now().plusDays(2).toString())
                .build();

        // When & Then
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().is5xxServerError());

        verify(bookingService, never()).createBooking(any(), anyLong());
    }

    @Test
    void updateBooking_shouldUpdateBooking() throws Exception {
        // Given
        when(bookingService.updateBooking(bookingId, true, userId))
                .thenReturn(testBookingDto);

        // When & Then
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(requestHeader, userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));

        verify(bookingService).updateBooking(bookingId, true, userId);
    }

    @Test
    void updateBooking_withFalseApproved_shouldUpdateBooking() throws Exception {
        // Given
        when(bookingService.updateBooking(bookingId, false, userId))
                .thenReturn(testBookingDto);

        // When & Then
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(requestHeader, userId)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));

        verify(bookingService).updateBooking(bookingId, false, userId);
    }

    @Test
    void updateBooking_withoutApprovedParam_shouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(requestHeader, userId))
                .andExpect(status().is5xxServerError());

        verify(bookingService, never()).updateBooking(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    void updateBooking_withInvalidApprovedParam_shouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(requestHeader, userId)
                        .param("approved", "invalid")) // не boolean
                .andExpect(status().is5xxServerError());

        verify(bookingService, never()).updateBooking(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    void updateBooking_whenBookingNotFound_shouldReturnNotFound() throws Exception {
        // Given
        when(bookingService.updateBooking(bookingId, true, userId))
                .thenThrow(new NotFoundException("Booking not found"));

        // When & Then
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(requestHeader, userId)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());

        verify(bookingService).updateBooking(bookingId, true, userId);
    }

    @Test
    void deleteBooking_shouldDeleteBooking() throws Exception {
        // Given
        doNothing().when(bookingService).deleteBooking(bookingId);

        // When & Then
        mockMvc.perform(delete("/bookings/{bookingId}", bookingId))
                .andExpect(status().isOk());

        verify(bookingService).deleteBooking(bookingId);
    }

    @Test
    void getAllUserBookings_whenNoBookings_shouldReturnEmptyList() throws Exception {
        // Given
        when(bookingService.getAllBookingsByUserId(userId)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/bookings/owner")
                        .header(requestHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(bookingService).getAllBookingsByUserId(userId);
    }

    @Test
    void createNewBooking_whenValidationFailsInService_shouldReturnBadRequest() throws Exception {
        // Given
        when(bookingService.createBooking(any(BookingAddDto.class), eq(userId)))
                .thenThrow(new ValidationException("Validation failed"));

        // When & Then
        mockMvc.perform(post("/bookings")
                        .header(requestHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookingAddDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService).createBooking(any(BookingAddDto.class), eq(userId));
    }
}