package ru.yandex.practicum.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.item.repository.ItemRepository;
import ru.yandex.practicum.request.dto.ItemRequestDto;
import ru.yandex.practicum.request.repository.ItemRequestRepository;
import ru.yandex.practicum.user.entity.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final Long userId = 1L;
    private final Long requestId = 1L;
    private final User testUser = User.builder()
            .id(userId)
            .name("Test User")
            .email("test@example.com")
            .build();

    @Test
    void getRequestById_whenUserNotFound_shouldThrowException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(requestId, userId));

        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).findById(anyLong());
    }

    @Test
    void getRequestById_whenRequestNotFound_shouldThrowException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(requestId, userId));

        verify(userRepository).findById(userId);
        verify(itemRequestRepository).findById(requestId);
        verify(itemRepository, never()).findAllByRequestId(anyLong());
    }

    @Test
    void createRequest_whenUserNotFound_shouldThrowException() {
        // Given
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a laptop")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
                itemRequestService.createRequest(requestDto, userId));

        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getCurrentRequests_whenUserNotFound_shouldThrowException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getCurrentRequests(userId));

        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).findAllByRequesterIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getCurrentRequests_whenNoRequests_shouldReturnEmptyList() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId))
                .thenReturn(List.of());

        // When
        List<ItemRequestDto> result = itemRequestService.getCurrentRequests(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findById(userId);
        verify(itemRequestRepository).findAllByRequesterIdOrderByCreatedDesc(userId);
    }


    @Test
    void getAllRequests_whenUserNotFound_shouldThrowException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getAllRequests(userId));

        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).findAllByRequesterIdNotOrderByCreatedDesc(anyLong());
    }

    @Test
    void getAllRequests_whenNoOtherRequests_shouldReturnEmptyList() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId))
                .thenReturn(List.of());

        // When
        List<ItemRequestDto> result = itemRequestService.getAllRequests(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findById(userId);
        verify(itemRequestRepository).findAllByRequesterIdNotOrderByCreatedDesc(userId);
    }

}