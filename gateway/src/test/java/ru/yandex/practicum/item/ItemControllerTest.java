package ru.yandex.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.item.dto.CommentDto;
import ru.yandex.practicum.item.dto.ItemDto;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@DisplayName("Тесты ItemController")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private final String headerName = "X-Sharer-User-Id";

    @Test
    @DisplayName("Должен получить предмет по ID")
    void shouldGetItemById() throws Exception {
        // given
        Long itemId = 1L;
        Long userId = 1L;
        String expectedResponse = """
            {
                "id": 1,
                "name": "Test Item",
                "description": "Test Description",
                "available": true
            }
            """;

        when(itemClient.getItemById(itemId, userId))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(get("/items/{id}", itemId)
                        .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен получить все предметы пользователя")
    void shouldGetAllItemsByUserId() throws Exception {
        // given
        Long userId = 1L;
        String expectedResponse = """
            [{
                "id": 1,
                "name": "Item 1",
                "available": true
            }, {
                "id": 2,
                "name": "Item 2",
                "available": false
            }]
            """;

        when(itemClient.getAllItemsByUserId(userId))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(get("/items")
                        .header(headerName, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен создать новый предмет")
    void shouldPostItem() throws Exception {
        // given
        Long userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .build();

        String expectedResponse = """
            {
                "id": 1,
                "name": "New Item",
                "description": "New Description",
                "available": true
            }
            """;

        when(itemClient.createItem(eq(userId), any(ItemDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(expectedResponse));

        // when & then
        mockMvc.perform(post("/items")
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен обновить предмет")
    void shouldPatchItem() throws Exception {
        // given
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        String expectedResponse = """
            {
                "id": 1,
                "name": "Updated Item",
                "description": "Updated Description",
                "available": false
            }
            """;

        when(itemClient.updateItem(eq(itemId), eq(userId), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(patch("/items/{id}", itemId)
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен выполнить поиск по тексту")
    void shouldSearchByText() throws Exception {
        // given
        String searchText = "test";
        String expectedResponse = """
            [{
                "id": 1,
                "name": "Test Item",
                "description": "Test Description",
                "available": true
            }]
            """;

        when(itemClient.searchByText(searchText))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Должен вернуть пустой список при пустом тексте поиска")
    void shouldReturnEmptyListForBlankSearchText() throws Exception {
        // given
        String blankText = "   ";

        // when & then
        mockMvc.perform(get("/items/search")
                        .param("text", blankText))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("Должен создать комментарий")
    void shouldPostComment() throws Exception {
        // given
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .withText("Great item!")
                .build();

        String expectedResponse = """
            {
                "id": 1,
                "text": "Great item!",
                "authorName": "User1",
                "created": "2023-12-01T10:00:00"
            }
            """;

        when(itemClient.createComment(eq(userId), eq(itemId), any(CommentDto.class)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // when & then
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(headerName, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}