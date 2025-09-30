package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getAllUsersItems(Long userId) {
        return itemRepository.getAllUsersItems(userId)
                .stream()
                .map(ItemMapper::entityItemToDto)
                .toList();
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item itemEntity = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с данным ID не найдена!"));
        return ItemMapper.entityItemToDto(itemEntity);
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text.isEmpty()) return new ArrayList<>();
        return itemRepository.getItemsByText(text)
                .stream()
                .map(ItemMapper::entityItemToDto)
                .toList();
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        hasUser(userId);
        Item itemEntity = itemRepository.addItem(itemDto, userId);
        return ItemMapper.entityItemToDto(itemEntity);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        hasUser(userId);
        Item itemEntity = itemRepository.updateItem(itemDto, itemId, userId);
        return ItemMapper.entityItemToDto(itemEntity);
    }

    @Override
    public void deleteById(long itemId) {
        itemRepository.deleteById(itemId);
    }

    private void hasUser(long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с ID %d - не существует!".formatted(userId)));
    }
}
