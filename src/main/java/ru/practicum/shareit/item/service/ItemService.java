package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllUsersItems(Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getItemsByText(String text);

    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    void deleteById(long itemId);
}
