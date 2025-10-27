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
@DisplayName("Тесты валидации ItemRequestController")
class ItemRequestValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private final String headerName = "X-Sharer-User-Id";

    @Test
    @DisplayName("Должен принять валидный ItemRequestDto")
    void shouldAcceptValidItemRequestDto() throws Exception {
        // given
        Long userId = 1L;
        ItemRequestDto validDto = ItemRequestDto.builder()
                .description("Valid description for item request")
                .build();

        when(itemRequestClient.createItemRequest(any(ItemRequestDto.class), eq(userId)))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mockMvc.perform(post("/requests")
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Должен принять очень длинное описание")
    void shouldAcceptVeryLongDescription() throws Exception {
        // given
        Long userId = 1L;
        String longDescription = "A".repeat(1000);
        ItemRequestDto validDto = ItemRequestDto.builder()
                .description(longDescription)
                .build();

        when(itemRequestClient.createItemRequest(any(ItemRequestDto.class), eq(userId)))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mockMvc.perform(post("/requests")
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk());
    }
}