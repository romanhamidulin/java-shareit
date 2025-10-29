package ru.yandex.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.item.dto.ItemDtoWithDate;
import ru.yandex.practicum.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private final Long userId = 1L;
    private final Long itemId = 1L;
    private final String requestHeader = "X-Sharer-User-Id";

    private final ItemDto testItemDto = ItemDto.builder()
            .id(itemId)
            .name("Test Item")
            .description("Test Description")
            .available(true)
            .build();

    private final ItemDtoWithDate testItemDtoWithDate = ItemDtoWithDate.builder()
            .id(itemId)
            .name("Test Item")
            .description("Test Description")
            .available(true)
            .build();

    @Test
    void getAllUsersItems_shouldReturnUserItems() throws Exception {
        // Given
        List<ItemDto> items = List.of(testItemDto);
        when(itemService.getAllUsersItems(userId)).thenReturn(items);

        // When & Then
        mockMvc.perform(get("/items")
                        .header(requestHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value("Test Item"))
                .andExpect(jsonPath("$[0].description").value("Test Description"));

        verify(itemService).getAllUsersItems(userId);
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        // Given
        when(itemService.getItemById(itemId, userId)).thenReturn(testItemDtoWithDate);

        // When & Then
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(requestHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Test Item"));

        verify(itemService).getItemById(itemId, userId);
    }

    @Test
    void getItemById_whenItemNotFound_shouldReturnNotFound() throws Exception {
        // Given
        when(itemService.getItemById(itemId, userId))
                .thenThrow(new NotFoundException("Item not found"));

        // When & Then
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(requestHeader, userId))
                .andExpect(status().isNotFound());

        verify(itemService).getItemById(itemId, userId);
    }

    @Test
    void getSearchingItemsByText_shouldReturnMatchingItems() throws Exception {
        // Given
        String searchText = "test";
        List<ItemDto> items = List.of(testItemDto);
        when(itemService.getItemsByText(searchText)).thenReturn(items);

        // When & Then
        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value("Test Item"));

        verify(itemService).getItemsByText(searchText);
    }

    @Test
    void getSearchingItemsByText_withEmptyText_shouldReturnEmptyList() throws Exception {
        // Given
        when(itemService.getItemsByText("")).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(itemService).getItemsByText("");
    }

    @Test
    void createNewItem_shouldCreateItem() throws Exception {
        // Given
        ItemDto createDto = ItemDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .build();

        when(itemService.addItem(any(ItemDto.class), eq(userId))).thenReturn(testItemDto);

        // When & Then
        mockMvc.perform(post("/items")
                        .header(requestHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Test Item"));

        verify(itemService).addItem(any(ItemDto.class), eq(userId));
    }

    @Test
    void updateItem_shouldUpdateItem() throws Exception {
        // Given
        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name")
                .build();

        ItemDto updatedItem = ItemDto.builder()
                .id(itemId)
                .name("Updated Name")
                .description("Original Description")
                .available(true)
                .build();

        when(itemService.updateItem(any(ItemDto.class), eq(itemId), eq(userId)))
                .thenReturn(updatedItem);

        // When & Then
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(requestHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Updated Name"));

        verify(itemService).updateItem(any(ItemDto.class), eq(itemId), eq(userId));
    }

    @Test
    void updateItem_withPartialData_shouldUpdateOnlyProvidedFields() throws Exception {
        // Given - обновляем только описание
        ItemDto partialUpdate = ItemDto.builder()
                .description("Only description updated")
                .build();

        ItemDto updatedItem = ItemDto.builder()
                .id(itemId)
                .name("Original Name") // имя осталось прежним
                .description("Only description updated")
                .available(true)
                .build();

        when(itemService.updateItem(any(ItemDto.class), eq(itemId), eq(userId)))
                .thenReturn(updatedItem);

        // When & Then
        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(requestHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Original Name"))
                .andExpect(jsonPath("$.description").value("Only description updated"));

        verify(itemService).updateItem(any(ItemDto.class), eq(itemId), eq(userId));
    }

    @Test
    void deleteItem_shouldDeleteItem() throws Exception {
        // Given
        doNothing().when(itemService).deleteById(itemId);

        // When & Then
        mockMvc.perform(delete("/items/{itemId}", itemId))
                .andExpect(status().isOk());

        verify(itemService).deleteById(itemId);
    }

    @Test
    void getAllUsersItems_whenNoItems_shouldReturnEmptyList() throws Exception {
        // Given
        when(itemService.getAllUsersItems(userId)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/items")
                        .header(requestHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(itemService).getAllUsersItems(userId);
    }

    @Test
    void getSearchingItemsByText_whenNoMatches_shouldReturnEmptyList() throws Exception {
        // Given
        when(itemService.getItemsByText("nonexistent")).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/items/search")
                        .param("text", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(itemService).getItemsByText("nonexistent");
    }
}