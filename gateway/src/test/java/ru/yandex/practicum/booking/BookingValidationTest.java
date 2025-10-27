package ru.yandex.practicum.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.booking.dto.BookingAddDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@DisplayName("Тесты валидации BookingController")
class BookingValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private final String headerName = "X-Sharer-User-Id";

    @Test
    @DisplayName("Должен вернуть 400 при создании бронирования с null itemId")
    void shouldReturnBadRequestWhenItemIdIsNull() throws Exception {
        // given
        BookingAddDto invalidDto = BookingAddDto.builder()
                .itemId(null) // невалидно
                .start("2025-10-01T10:00:00")
                .end("2025-10-10T10:00:00")
                .build();

        // when & then
        mockMvc.perform(post("/bookings")
                        .header(headerName, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен вернуть 400 при создании бронирования с null start")
    void shouldReturnBadRequestWhenStartIsNull() throws Exception {
        // given
        BookingAddDto invalidDto = BookingAddDto.builder()
                .itemId(1L)
                .start(null) // невалидно
                .end("2025-10-10T10:00:00")
                .build();

        // when & then
        mockMvc.perform(post("/bookings")
                        .header(headerName, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен вернуть 400 при создании бронирования с null end")
    void shouldReturnBadRequestWhenEndIsNull() throws Exception {
        // given
        BookingAddDto invalidDto = BookingAddDto.builder()
                .itemId(1L)
                .start("2025-10-01T10:00:00")
                .end(null) // невалидно
                .build();

        // when & then
        mockMvc.perform(post("/bookings")
                        .header(headerName, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен успешно создать бронирование с валидными данными")
    void shouldCreateBookingWithValidData() throws Exception {
        // given
        BookingAddDto validDto = BookingAddDto.builder()
                .itemId(1L)
                .start("2025-10-01T10:00:00")
                .end("2025-10-10T10:00:00")
                .build();

        when(bookingClient.createBooking(anyLong(), any(BookingAddDto.class)))
                .thenReturn(ResponseEntity.ok("{}"));

        // when & then
        mockMvc.perform(post("/bookings")
                        .header(headerName, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk());
    }
}