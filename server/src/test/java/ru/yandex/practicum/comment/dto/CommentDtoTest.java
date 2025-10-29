package ru.yandex.practicum.comment.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты JSON сериализации/десериализации CommentDto")
class CommentDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Должен сериализовать CommentDto в JSON")
    void shouldSerializeCommentDto() throws JsonProcessingException {
        // given
        LocalDateTime created = LocalDateTime.of(2023, 12, 1, 10, 0, 0);
        CommentDto commentDto = CommentDto.builder()
                .withId(1L)
                .withText("Great item! Very useful.")
                .withAuthorName("John Doe")
                .withCreated(created)
                .build();

        // when
        String json = objectMapper.writeValueAsString(commentDto);

        // then
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Great item! Very useful.\"");
        assertThat(json).contains("\"authorName\":\"John Doe\"");
        assertThat(json).contains("\"created\":[2023,12,1,10,0]");
    }

    @Test
    @DisplayName("Должен десериализовать JSON в CommentDto")
    void shouldDeserializeCommentDto() throws JsonProcessingException {
        // given
        String json = """
            {
                "id": 1,
                "text": "Excellent condition, fast delivery",
                "authorName": "Alice Smith",
                "created": "2023-12-01T10:00:00"
            }
            """;

        // when
        CommentDto result = objectMapper.readValue(json, CommentDto.class);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Excellent condition, fast delivery");
        assertThat(result.getAuthorName()).isEqualTo("Alice Smith");
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2023, 12, 1, 10, 0, 0));
    }

    @Test
    @DisplayName("Должен десериализовать JSON без опциональных полей")
    void shouldDeserializeWithoutOptionalFields() throws JsonProcessingException {
        // given
        String json = """
            {
                "text": "Simple comment"
            }
            """;

        // when
        CommentDto result = objectMapper.readValue(json, CommentDto.class);

        // then
        assertThat(result.getText()).isEqualTo("Simple comment");
        assertThat(result.getId()).isNull();
        assertThat(result.getAuthorName()).isNull();
        assertThat(result.getCreated()).isNull();
    }

    @Test
    @DisplayName("Должен корректно работать полный цикл сериализации-десериализации")
    void shouldWorkFullSerializationDeserializationCycle() throws JsonProcessingException {
        // given
        LocalDateTime created = LocalDateTime.of(2023, 12, 1, 10, 30, 45);
        CommentDto original = CommentDto.builder()
                .withId(1L)
                .withText("Test comment with special chars: \"quotes\" & 'apostrophes'")
                .withAuthorName("Test User")
                .withCreated(created)
                .build();

        // when
        String json = objectMapper.writeValueAsString(original);
        CommentDto deserialized = objectMapper.readValue(json, CommentDto.class);

        // then
        assertThat(deserialized.getId()).isEqualTo(original.getId());
        assertThat(deserialized.getText()).isEqualTo(original.getText());
        assertThat(deserialized.getAuthorName()).isEqualTo(original.getAuthorName());
        assertThat(deserialized.getCreated()).isEqualTo(original.getCreated());
    }

    @Test
    @DisplayName("Должен корректно обрабатывать стандартные форматы дат ISO")
    void shouldHandleStandardDateTimeFormats() throws JsonProcessingException {
        // given - только стандартные ISO форматы
        String[] isoDateFormats = {
                "2023-12-01T10:00:00",
                "2023-12-01T10:00:00.000",
                "2023-12-01T10:00"
        };

        for (String dateFormat : isoDateFormats) {
            String json = String.format("""
                {
                    "text": "Test comment",
                    "created": "%s"
                }
                """, dateFormat);

            // when
            CommentDto result = objectMapper.readValue(json, CommentDto.class);

            // then
            assertThat(result.getCreated()).isNotNull();
            assertThat(result.getText()).isEqualTo("Test comment");
        }
    }

    @Test
    @DisplayName("Должен обрабатывать даты с миллисекундами")
    void shouldHandleDatesWithMilliseconds() throws JsonProcessingException {
        // given
        String json = """
            {
                "text": "Test comment",
                "created": "2023-12-01T10:00:00.123"
            }
            """;

        // when
        CommentDto result = objectMapper.readValue(json, CommentDto.class);

        // then
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2023, 12, 1, 10, 0, 0, 123000000));
        assertThat(result.getText()).isEqualTo("Test comment");
    }
}