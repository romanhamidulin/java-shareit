package ru.yandex.practicum.server.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.booking.BookingClient;
import ru.yandex.practicum.booking.BookingController;
import ru.yandex.practicum.booking.dto.BookingAddDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@DisplayName("Тесты BookingController")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private final String headerName = "X-Sharer-User-Id";

    @Test
    @DisplayName("Должен получить все бронирования пользователя")
    void shouldGetAllUserBookings() throws Exception {
        // given
        Long userId = 1L;
        String expectedResponse = """
            [{
                "id": 1,
                "start": "2025-10-01T10:00:00",
                "end": "2025-10-10T10:00:00",
                "status": "WAITING"
            }]
            """;

        when(bookingClient.getBookingsByUser(userId))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(get("/bookings/owner")
                        .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен получить бронирование по ID")
    void shouldGetBookingById() throws Exception {
        // given
        Long userId = 1L;
        Long bookingId = 100L;
        String expectedResponse = """
            {
                "id": 100,
                "start": "2025-10-01T10:00:00",
                "end": "2025-10-10T10:00:00",
                "status": "APPROVED"
            }
            """;

        when(bookingClient.getBookingById(userId, bookingId))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен создать новое бронирование")
    void shouldCreateNewBooking() throws Exception {
        // given
        Long userId = 1L;
        BookingAddDto bookingAddDto = BookingAddDto.builder()
                .itemId(1L)
                .start("2025-10-01T10:00:00")
                .end("2025-10-10T10:00:00")
                .build();

        String expectedResponse = """
            {
                "id": 101,
                "start": "2025-10-01T10:00:00",
                "end": "2025-10-10T10:00:00",
                "status": "WAITING"
            }
            """;

        when(bookingClient.createBooking(eq(userId), any(BookingAddDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(post("/bookings")
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingAddDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен обновить статус бронирования")
    void shouldUpdateBooking() throws Exception {
        // given
        Long userId = 1L;
        Long bookingId = 100L;
        Boolean approved = true;
        String expectedResponse = """
            {
                "id": 100,
                "status": "APPROVED"
            }
            """;

        when(bookingClient.updateBooking(userId, bookingId, approved))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(headerName, userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен вернуть 400 при невалидном userId")
    void shouldReturnBadRequestForInvalidUserId() throws Exception {
        // when & then
        mockMvc.perform(get("/bookings/owner")
                        .header(headerName, "0")) // @Positive требует > 0
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен вернуть 400 при невалидном bookingId")
    void shouldReturnBadRequestForInvalidBookingId() throws Exception {
        // when & then
        mockMvc.perform(get("/bookings/{bookingId}", "0") // @Positive требует > 0
                        .header(headerName, "1"))
                .andExpect(status().isBadRequest());
    }
}