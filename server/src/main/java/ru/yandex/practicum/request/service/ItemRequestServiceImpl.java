package ru.yandex.practicum.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.item.dto.ItemShortDto;
import ru.yandex.practicum.item.mapper.ItemMapper;
import ru.yandex.practicum.item.repository.ItemRepository;
import ru.yandex.practicum.request.dto.ItemRequestDto;
import ru.yandex.practicum.request.entity.ItemRequest;
import ru.yandex.practicum.request.mapper.ItemRequestMapper;
import ru.yandex.practicum.request.repository.ItemRequestRepository;
import ru.yandex.practicum.user.entity.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        // Проверяем существование пользователя
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с ID %s не существует".formatted(userId)));

        // Находим запрос
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID = %d не найден".formatted(requestId)));

        // Находим предметы, связанные с этим запросом
        List<ItemShortDto> items = itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper::entityToShortDto)
                .collect(Collectors.toList());

        ItemRequestDto responseDto = ItemRequestMapper.toDto(itemRequest);
        responseDto.setItems(items);

        return responseDto;


    }

    @Transactional
    @Override
    public ItemRequestDto createRequest(ItemRequestDto dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с ID = %d не найден!", userId)));
        ItemRequest request = ItemRequestMapper.toEntity(dto, user);
        return ItemRequestMapper.toDto(itemRequestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> getCurrentRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с ID = %d не найден!", userId)));
        return itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с ID = %d не найден!", userId)));
        return itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId).stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }
}
