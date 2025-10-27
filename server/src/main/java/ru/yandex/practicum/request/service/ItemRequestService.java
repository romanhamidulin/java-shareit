package ru.yandex.practicum.request.service;

import ru.yandex.practicum.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto getRequestById(Long requestId, Long userId);

    List<ItemRequestDto> getCurrentRequests(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId);

    ItemRequestDto createRequest(ItemRequestDto dto, Long userId);
}