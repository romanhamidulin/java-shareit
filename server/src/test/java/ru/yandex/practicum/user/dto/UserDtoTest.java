package ru.yandex.practicum.user.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldSerializeToJson() throws JsonProcessingException {
        // Given
        ru.yandex.practicum.user.dto.UserDto userDto = ru.yandex.practicum.user.dto.UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        // When
        String json = objectMapper.writeValueAsString(userDto);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"name\":\"John Doe\""));
        assertTrue(json.contains("\"email\":\"john.doe@example.com\""));
    }

    @Test
    void shouldSerializeWithoutId() throws JsonProcessingException {
        // Given
        ru.yandex.practicum.user.dto.UserDto userDto = ru.yandex.practicum.user.dto.UserDto.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .build();

        // When
        String json = objectMapper.writeValueAsString(userDto);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"Jane Smith\""));
        assertTrue(json.contains("\"email\":\"jane.smith@example.com\""));
        assertTrue(json.contains("\"id\":null") || !json.contains("\"id\""));
    }

    @Test
    void shouldDeserializeFromJson() throws JsonProcessingException {
        // Given
        String json = """
            {
                "id": 1,
                "name": "John Doe",
                "email": "john.doe@example.com"
            }
            """;

        // When
        ru.yandex.practicum.user.dto.UserDto userDto = objectMapper.readValue(json, ru.yandex.practicum.user.dto.UserDto.class);

        // Then
        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertEquals("John Doe", userDto.getName());
        assertEquals("john.doe@example.com", userDto.getEmail());
    }

    @Test
    void shouldDeserializeFromJsonWithoutId() throws JsonProcessingException {
        // Given
        String json = """
            {
                "name": "Jane Smith",
                "email": "jane.smith@example.com"
            }
            """;

        // When
        ru.yandex.practicum.user.dto.UserDto userDto = objectMapper.readValue(json, ru.yandex.practicum.user.dto.UserDto.class);

        // Then
        assertNotNull(userDto);
        assertNull(userDto.getId());
        assertEquals("Jane Smith", userDto.getName());
        assertEquals("jane.smith@example.com", userDto.getEmail());
    }

    @Test
    void shouldDeserializeFromJsonWithNullFields() throws JsonProcessingException {
        // Given
        String json = """
            {
                "id": null,
                "name": "Test User",
                "email": null
            }
            """;

        // When
        ru.yandex.practicum.user.dto.UserDto userDto = objectMapper.readValue(json, ru.yandex.practicum.user.dto.UserDto.class);

        // Then
        assertNotNull(userDto);
        assertNull(userDto.getId());
        assertEquals("Test User", userDto.getName());
        assertNull(userDto.getEmail());
    }

    @Test
    void shouldHandleMinLengthName() throws JsonProcessingException {
        // Given
        String json = """
            {
                "name": "A",
                "email": "test@example.com"
            }
            """;

        // When
        ru.yandex.practicum.user.dto.UserDto userDto = objectMapper.readValue(json, ru.yandex.practicum.user.dto.UserDto.class);

        // Then
        assertNotNull(userDto);
        assertEquals("A", userDto.getName());
    }

    @Test
    void shouldHandleMaxLengthName() throws JsonProcessingException {
        // Given
        String maxLengthName = "A".repeat(30);
        String json = String.format("""
            {
                "name": "%s",
                "email": "test@example.com"
            }
            """, maxLengthName);

        // When
        ru.yandex.practicum.user.dto.UserDto userDto = objectMapper.readValue(json, ru.yandex.practicum.user.dto.UserDto.class);

        // Then
        assertNotNull(userDto);
        assertEquals(maxLengthName, userDto.getName());
    }

    @Test
    void shouldSerializeAndDeserializeConsistently() throws JsonProcessingException {
        // Given
        ru.yandex.practicum.user.dto.UserDto originalUser = ru.yandex.practicum.user.dto.UserDto.builder()
                .id(123L)
                .name("Consistency Test")
                .email("consistency@test.com")
                .build();

        // When
        String json = objectMapper.writeValueAsString(originalUser);
        ru.yandex.practicum.user.dto.UserDto deserializedUser = objectMapper.readValue(json, ru.yandex.practicum.user.dto.UserDto.class);

        // Then
        assertEquals(originalUser.getId(), deserializedUser.getId());
        assertEquals(originalUser.getName(), deserializedUser.getName());
        assertEquals(originalUser.getEmail(), deserializedUser.getEmail());
    }

    @Test
    void shouldHandleEmptyObject() throws JsonProcessingException {
        // Given
        String emptyJson = "{}";

        // When
        ru.yandex.practicum.user.dto.UserDto userDto = objectMapper.readValue(emptyJson, ru.yandex.practicum.user.dto.UserDto.class);

        // Then
        assertNotNull(userDto);
        assertNull(userDto.getId());
        assertNull(userDto.getName());
        assertNull(userDto.getEmail());
    }

    @Test
    void shouldHandleDifferentEmailFormats() throws JsonProcessingException {
        // Given
        String[] validEmails = {
                "simple@example.com",
                "very.common@example.com",
                "disposable.style.email.with+symbol@example.com",
                "other.email-with-hyphen@example.com",
                "fully-qualified-domain@example.com",
                "user.name+tag+sorting@example.com",
                "x@example.com",
                "example-indeed@strange-example.com",
                "test@example.co.uk"
        };

        for (String email : validEmails) {
            String json = String.format("""
                {
                    "name": "Test User",
                    "email": "%s"
                }
                """, email);

            // When
            ru.yandex.practicum.user.dto.UserDto userDto = objectMapper.readValue(json, UserDto.class);

            // Then
            assertNotNull(userDto);
            assertEquals(email, userDto.getEmail());
        }
    }
}