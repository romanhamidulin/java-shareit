package ru.yandex.practicum.item.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты JSON сериализации/десериализации ItemDtoWithDate")
class ItemDtoWithDateTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private CommentDto createTestCommentDto() {
        return CommentDto.builder()
                .withId(1L)
                .withText("Great item!")
                .withAuthorName("John Doe")
                .withCreated(LocalDateTime.of(2023, 12, 1, 10, 0, 0))
                .build();
    }

    @Test
    @DisplayName("Должен сериализовать ItemDtoWithDate в JSON")
    void shouldSerializeItemDtoWithDate() throws JsonProcessingException {
        // given
        LocalDateTime lastBooking = LocalDateTime.of(2023, 11, 15, 14, 30, 0);
        LocalDateTime nextBooking = LocalDateTime.of(2023, 12, 10, 9, 0, 0);

        ru.yandex.practicum.item.dto.ItemDtoWithDate itemDto = ru.yandex.practicum.item.dto.ItemDtoWithDate.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .comments(List.of(createTestCommentDto()))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();

        // when
        String json = objectMapper.writeValueAsString(itemDto);

        // then
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test Item\"");
        assertThat(json).contains("\"description\":\"Test Description\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"comments\":");
        assertThat(json).contains("\"lastBooking\":[2023,11,15,14,30]");
        assertThat(json).contains("\"nextBooking\":[2023,12,10,9,0]");
    }

    @Test
    @DisplayName("Должен десериализовать JSON в ItemDtoWithDate")
    void shouldDeserializeItemDtoWithDate() throws JsonProcessingException {
        // given
        String json = """
            {
                "id": 1,
                "name": "Test Item",
                "description": "Test Description",
                "available": true,
                "comments": [
                    {
                        "id": 1,
                        "text": "Great item!",
                        "authorName": "John Doe",
                        "created": "2023-12-01T10:00:00"
                    }
                ],
                "lastBooking": "2023-11-15T14:30:00",
                "nextBooking": "2023-12-10T09:00:00"
            }
            """;

        // when
        ru.yandex.practicum.item.dto.ItemDtoWithDate result = objectMapper.readValue(json, ru.yandex.practicum.item.dto.ItemDtoWithDate.class);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getText()).isEqualTo("Great item!");
        assertThat(result.getLastBooking()).isEqualTo(LocalDateTime.of(2023, 11, 15, 14, 30, 0));
        assertThat(result.getNextBooking()).isEqualTo(LocalDateTime.of(2023, 12, 10, 9, 0, 0));
    }

    @Test
    @DisplayName("Должен десериализовать JSON без опциональных полей")
    void shouldDeserializeWithoutOptionalFields() throws JsonProcessingException {
        // given
        String json = """
            {
                "name": "Test Item",
                "description": "Test Description",
                "available": true
            }
            """;

        // when
        ru.yandex.practicum.item.dto.ItemDtoWithDate result = objectMapper.readValue(json, ru.yandex.practicum.item.dto.ItemDtoWithDate.class);

        // then
        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getId()).isNull();
        assertThat(result.getComments()).isNull();
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
    }

    @Test
    @DisplayName("Должен десериализовать с пустым списком комментариев")
    void shouldDeserializeWithEmptyCommentsList() throws JsonProcessingException {
        // given
        String json = """
            {
                "name": "Test Item",
                "description": "Test Description",
                "available": true,
                "comments": []
            }
            """;

        // when
        ru.yandex.practicum.item.dto.ItemDtoWithDate result = objectMapper.readValue(json, ru.yandex.practicum.item.dto.ItemDtoWithDate.class);

        // then
        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getComments()).isEmpty();
    }

    @Test
    @DisplayName("Должен корректно работать полный цикл сериализации-десериализации")
    void shouldWorkFullSerializationDeserializationCycle() throws JsonProcessingException {
        // given
        LocalDateTime lastBooking = LocalDateTime.of(2023, 11, 15, 14, 30, 0);
        LocalDateTime nextBooking = LocalDateTime.of(2023, 12, 10, 9, 0, 0);

        ru.yandex.practicum.item.dto.ItemDtoWithDate original = ru.yandex.practicum.item.dto.ItemDtoWithDate.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description with special chars: \"quotes\" & 'apostrophes'")
                .available(true)
                .comments(List.of(createTestCommentDto()))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();

        // when
        String json = objectMapper.writeValueAsString(original);
        ru.yandex.practicum.item.dto.ItemDtoWithDate deserialized = objectMapper.readValue(json, ItemDtoWithDate.class);

        // then
        assertThat(deserialized.getId()).isEqualTo(original.getId());
        assertThat(deserialized.getName()).isEqualTo(original.getName());
        assertThat(deserialized.getDescription()).isEqualTo(original.getDescription());
        assertThat(deserialized.getAvailable()).isEqualTo(original.getAvailable());
        assertThat(deserialized.getComments()).hasSize(1);
        assertThat(deserialized.getLastBooking()).isEqualTo(original.getLastBooking());
        assertThat(deserialized.getNextBooking()).isEqualTo(original.getNextBooking());
    }

}