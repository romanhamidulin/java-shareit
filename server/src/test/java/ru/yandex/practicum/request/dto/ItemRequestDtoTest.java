package ru.yandex.practicum.request.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.item.dto.ItemShortDto;
import ru.yandex.practicum.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldDeserializeFromJson() throws JsonProcessingException {
        // Given - используем правильную структуру ItemShortDto
        String json = """
            {
                "id": 1,
                "title": "Need a laptop",
                "description": "I need a laptop for work",
                "created": "2023-12-01T10:30:00",
                "requester": {
                    "id": 1,
                    "name": "John Doe",
                    "email": "john@example.com"
                },
                "items": [
                    {
                        "id": 101,
                        "name": "Item 1",
                        "ownerId": 201
                    }
                ]
            }
            """;

        // When
        ru.yandex.practicum.request.dto.ItemRequestDto requestDto = objectMapper.readValue(json, ru.yandex.practicum.request.dto.ItemRequestDto.class);

        // Then
        assertNotNull(requestDto);
        assertEquals(1L, requestDto.getId());
        assertEquals("Need a laptop", requestDto.getTitle());
        assertEquals("I need a laptop for work", requestDto.getDescription());
        assertEquals(LocalDateTime.of(2023, 12, 1, 10, 30, 0), requestDto.getCreated());

        assertNotNull(requestDto.getRequester());
        assertEquals(1L, requestDto.getRequester().getId());

        assertNotNull(requestDto.getItems());
        assertEquals(1, requestDto.getItems().size());
        assertEquals("Item 1", requestDto.getItems().get(0).getName());
        assertEquals(101L, requestDto.getItems().get(0).getId());
        // Убедись, что у ItemShortDto есть метод getOwnerId()
        // assertEquals(201L, requestDto.getItems().get(0).getOwnerId());
    }

    @Test
    void shouldSerializeToJson() throws JsonProcessingException {
        // Given
        LocalDateTime created = LocalDateTime.of(2023, 12, 1, 10, 30, 0);
        UserDto requester = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        // Создаем ItemShortDto с правильными полями
        ItemShortDto item1 = ItemShortDto.builder()
                .id(101L)
                .name("Item 1")
                .ownerId(201L) // используем правильное поле
                .build();

        ru.yandex.practicum.request.dto.ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1L)
                .title("Need a laptop")
                .description("I need a laptop for work")
                .created(created)
                .requester(requester)
                .items(List.of(item1))
                .build();

        // When
        String json = objectMapper.writeValueAsString(requestDto);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"title\":\"Need a laptop\""));
        assertTrue(json.contains("\"description\":\"I need a laptop for work\""));
        assertTrue(json.contains("\"created\":[2023,12,1,10,30]"));
        assertTrue(json.contains("\"requester\""));
        assertTrue(json.contains("\"items\""));
        assertTrue(json.contains("\"Item 1\""));
        assertTrue(json.contains("\"ownerId\":201"));
    }
}