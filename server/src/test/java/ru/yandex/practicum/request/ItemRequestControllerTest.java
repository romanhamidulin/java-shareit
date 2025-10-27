package ru.yandex.practicum.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.dto.ItemRequestDto;
import ru.yandex.practicum.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private final Long userId = 1L;
    private final Long requestId = 1L;
    private final String requestHeader = "X-Sharer-User-Id";

    private final ItemRequestDto testRequestDto = ItemRequestDto.builder()
            .id(requestId)
            .description("Need a laptop for work")
            .created(LocalDateTime.now())
            .build();

    @Test
    void getCurrentRequests_shouldReturnUserRequests() throws Exception {
        // Given
        List<ItemRequestDto> requests = List.of(testRequestDto);
        when(itemRequestService.getCurrentRequests(userId)).thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/requests")
                        .header(requestHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].description").value("Need a laptop for work"));

        verify(itemRequestService).getCurrentRequests(userId);
    }

    @Test
    void getAllRequests_shouldReturnOtherUsersRequests() throws Exception {
        // Given
        List<ItemRequestDto> requests = List.of(testRequestDto);
        when(itemRequestService.getAllRequests(userId)).thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/requests/all")
                        .header(requestHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestId));

        verify(itemRequestService).getAllRequests(userId);
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        // Given
        when(itemRequestService.getRequestById(requestId, userId)).thenReturn(testRequestDto);

        // When & Then
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(requestHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need a laptop for work"));

        verify(itemRequestService).getRequestById(requestId, userId);
    }

    @Test
    void getRequestById_whenRequestNotFound_shouldReturnNotFound() throws Exception {
        // Given
        when(itemRequestService.getRequestById(requestId, userId))
                .thenThrow(new NotFoundException("Запрос не найден"));

        // When & Then
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(requestHeader, userId))
                .andExpect(status().isNotFound());

        verify(itemRequestService).getRequestById(requestId, userId);
    }

    @Test
    void createRequest_shouldCreateRequest() throws Exception {
        // Given
        ItemRequestDto createDto = ItemRequestDto.builder()
                .description("Need a laptop for work")
                .build();

        when(itemRequestService.createRequest(any(ItemRequestDto.class), eq(userId)))
                .thenReturn(testRequestDto);

        // When & Then
        mockMvc.perform(post("/requests")
                        .header(requestHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need a laptop for work"));

        verify(itemRequestService).createRequest(any(ItemRequestDto.class), eq(userId));
    }

    @Test
    void getCurrentRequests_whenNoRequests_shouldReturnEmptyList() throws Exception {
        // Given
        when(itemRequestService.getCurrentRequests(userId)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/requests")
                        .header(requestHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(itemRequestService).getCurrentRequests(userId);
    }

    @Test
    void getAllRequests_whenNoOtherRequests_shouldReturnEmptyList() throws Exception {
        // Given
        when(itemRequestService.getAllRequests(userId)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/requests/all")
                        .header(requestHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(itemRequestService).getAllRequests(userId);
    }

    @Test
    void createRequest_withValidData_shouldIgnoreIdInRequestBody() throws Exception {
        // Given
        ItemRequestDto createDto = ItemRequestDto.builder()
                .id(999L) // ID в теле запроса (должен игнорироваться)
                .description("Valid description")
                .build();

        when(itemRequestService.createRequest(any(ItemRequestDto.class), eq(userId)))
                .thenReturn(testRequestDto); // сервис возвращает свой ID

        // When & Then
        mockMvc.perform(post("/requests")
                        .header(requestHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId)); // проверяем что вернулся ID от сервиса

        verify(itemRequestService).createRequest(any(ItemRequestDto.class), eq(userId));
    }
}