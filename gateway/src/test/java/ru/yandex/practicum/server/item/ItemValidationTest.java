package ru.yandex.practicum.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.item.ItemClient;
import ru.yandex.practicum.item.ItemController;
import ru.yandex.practicum.item.dto.CommentDto;
import ru.yandex.practicum.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@DisplayName("Тесты валидации ItemController")
class ItemValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private final String headerName = "X-Sharer-User-Id";



    @Test
    @DisplayName("Должен вернуть 400 при создании предмета с пустым именем")
    void shouldReturnBadRequestWhenNameIsEmpty() throws Exception {
        // given
        Long userId = 1L;
        ItemDto invalidDto = ItemDto.builder()
                .name("") // невалидно
                .description("Description")
                .available(true)
                .build();

        // when & then
        mockMvc.perform(post("/items")
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен вернуть 400 при создании предмета без available")
    void shouldReturnBadRequestWhenAvailableIsNull() throws Exception {
        // given
        Long userId = 1L;
        ItemDto invalidDto = ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(null) // невалидно
                .build();

        // when & then
        mockMvc.perform(post("/items")
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен вернуть 400 при создании комментария без текста")
    void shouldReturnBadRequestWhenCommentTextIsNull() throws Exception {
        // given
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto invalidDto = CommentDto.builder()
                .withText(null) // невалидно
                .build();

        // when & then
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен вернуть 400 при создании комментария с пустым текстом")
    void shouldReturnBadRequestWhenCommentTextIsEmpty() throws Exception {
        // given
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto invalidDto = CommentDto.builder()
                .withText("") // невалидно
                .build();

        // when & then
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен вернуть 400 при невалидном userId")
    void shouldReturnBadRequestForInvalidUserId() throws Exception {
        // given
        Long invalidUserId = 0L; // @Positive требует > 0

        // when & then
        mockMvc.perform(get("/items")
                        .header(headerName, invalidUserId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен вернуть 400 при невалидном itemId")
    void shouldReturnBadRequestForInvalidItemId() throws Exception {
        // given
        Long invalidItemId = 0L; // @Positive требует > 0
        Long userId = 1L;

        // when & then
        mockMvc.perform(get("/items/{id}", invalidItemId)
                        .header(headerName, userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен принять валидный ItemDto")
    void shouldAcceptValidItemDto() throws Exception {
        // given
        Long userId = 1L;
        ItemDto validDto = ItemDto.builder()
                .name("Valid Item")
                .description("Valid Description")
                .available(true)
                .build();

        when(itemClient.createItem(eq(userId), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mockMvc.perform(post("/items")
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk());
    }
}