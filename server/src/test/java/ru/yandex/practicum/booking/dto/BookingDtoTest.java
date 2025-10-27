package ru.yandex.practicum.booking.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.booking.entity.BookingStatus;
import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты JSON сериализации/десериализации BookingDto")
class BookingDtoTest {

    private ObjectMapper objectMapper;
    private ItemDto testItem;
    private UserDto testBooker;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        testItem = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        testBooker = UserDto.builder()
                .id(1L)
                .name("Test Booker")
                .email("booker@example.com")
                .build();
    }

    @Test
    @DisplayName("Должен сериализовать BookingDto в JSON")
    void shouldSerializeBookingDto() throws JsonProcessingException {
        // given
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start("2023-12-01T10:00:00")
                .end("2023-12-10T10:00:00")
                .item(testItem)
                .status(BookingStatus.WAITING)
                .booker(testBooker)
                .build();

        // when
        String json = objectMapper.writeValueAsString(bookingDto);

        // then
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"start\":\"2023-12-01T10:00:00\"");
        assertThat(json).contains("\"end\":\"2023-12-10T10:00:00\"");
        assertThat(json).contains("\"status\":\"WAITING\"");
        assertThat(json).contains("\"item\":");
        assertThat(json).contains("\"booker\":");
    }

    @Test
    @DisplayName("Должен десериализовать JSON в BookingDto")
    void shouldDeserializeBookingDto() throws JsonProcessingException {
        // given
        String json = """
            {
                "id": 1,
                "start": "2023-12-01T10:00:00",
                "end": "2023-12-10T10:00:00",
                "item": {
                    "id": 1,
                    "name": "Test Item",
                    "description": "Test Description",
                    "available": true
                },
                "status": "APPROVED",
                "booker": {
                    "id": 1,
                    "name": "Test Booker",
                    "email": "booker@example.com"
                }
            }
            """;

        // when
        BookingDto result = objectMapper.readValue(json, BookingDto.class);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo("2023-12-01T10:00:00");
        assertThat(result.getEnd()).isEqualTo("2023-12-10T10:00:00");
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(1L);
        assertThat(result.getBooker()).isNotNull();
        assertThat(result.getBooker().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Должен десериализовать без опционального поля booker")
    void shouldDeserializeWithoutBooker() throws JsonProcessingException {
        // given
        String json = """
            {
                "id": 1,
                "start": "2023-12-01T10:00:00",
                "end": "2023-12-10T10:00:00",
                "item": {
                    "id": 1,
                    "name": "Test Item",
                    "available": true
                },
                "status": "WAITING"
            }
            """;

        // when
        BookingDto result = objectMapper.readValue(json, BookingDto.class);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo("2023-12-01T10:00:00");
        assertThat(result.getEnd()).isEqualTo("2023-12-10T10:00:00");
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getBooker()).isNull();
    }

    @Test
    @DisplayName("Должен сериализовать все статусы бронирования")
    void shouldSerializeAllBookingStatuses() throws JsonProcessingException {
        // given
        BookingStatus[] statuses = BookingStatus.values();

        for (BookingStatus status : statuses) {
            BookingDto bookingDto = BookingDto.builder()
                    .id(1L)
                    .start("2023-12-01T10:00:00")
                    .end("2023-12-10T10:00:00")
                    .item(testItem)
                    .status(status)
                    .build();

            // when
            String jsonString = objectMapper.writeValueAsString(bookingDto);

            // then
            assertThat(jsonString).contains("\"status\":\"" + status + "\"");
        }
    }

    @Test
    @DisplayName("Должен десериализовать все статусы бронирования")
    void shouldDeserializeAllBookingStatuses() throws JsonProcessingException {
        // given
        BookingStatus[] statuses = BookingStatus.values();

        for (BookingStatus status : statuses) {
            String json = String.format("""
                {
                    "id": 1,
                    "start": "2023-12-01T10:00:00",
                    "end": "2023-12-10T10:00:00",
                    "item": {
                        "id": 1,
                        "name": "Test Item",
                        "available": true
                    },
                    "status": "%s"
                }
                """, status);

            // when
            BookingDto result = objectMapper.readValue(json, BookingDto.class);

            // then
            assertThat(result.getStatus()).isEqualTo(status);
        }
    }

}