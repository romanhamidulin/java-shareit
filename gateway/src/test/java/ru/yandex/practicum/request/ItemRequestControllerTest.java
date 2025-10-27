package ru.yandex.practicum.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@DisplayName("Тесты ItemRequestController")
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private final String headerName = "X-Sharer-User-Id";

    @Test
    @DisplayName("Должен получить текущие запросы пользователя")
    void shouldGetCurrentRequests() throws Exception {
        // given
        Long userId = 1L;
        String expectedResponse = """
            [{
                "id": 1,
                "description": "Need a drill",
                "created": "2025-10-01T10:00:00"
            }]
            """;

        when(itemRequestClient.getItemRequests(userId))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(get("/requests")
                        .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен получить все запросы")
    void shouldGetAllRequests() throws Exception {
        // given
        Long userId = 1L;
        String expectedResponse = """
            [{
                "id": 1,
                "description": "Need a drill",
                "created": "2025-10-01T10:00:00"
            }, {
                "id": 2,
                "description": "Looking for a ladder",
                "created": "2025-10-02T10:00:00"
            }]
            """;

        when(itemRequestClient.getAllItemRequests(userId))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(get("/requests/all")
                        .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен получить запрос по ID")
    void shouldGetRequestById() throws Exception {
        // given
        Long requestId = 1L;
        Long userId = 1L;
        String expectedResponse = """
            {
                "id": 1,
                "description": "Need a drill",
                "created": "2025-10-01T10:00:00",
                "items": []
            }
            """;

        when(itemRequestClient.getItemRequestById(requestId, userId))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен создать новый запрос")
    void shouldCreateRequest() throws Exception {
        // given
        Long userId = 1L;
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a power saw")
                .build();

        String expectedResponse = """
            {
                "id": 1,
                "description": "Need a power saw",
                "created": "2025-10-01T10:00:00"
            }
            """;

        when(itemRequestClient.createItemRequest(any(ItemRequestDto.class), eq(userId)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(post("/requests")
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен создать запрос с минимальным описанием")
    void shouldCreateRequestWithMinimalDescription() throws Exception {
        // given
        Long userId = 1L;
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("A") // минимальное описание
                .build();

        when(itemRequestClient.createItemRequest(any(ItemRequestDto.class), eq(userId)))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mockMvc.perform(post("/requests")
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }
}