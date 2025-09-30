package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> getAllUsersItems(long userId);

    Optional<Item> getItemById(long itemId);

    List<Item> getItemsByText(String text);

    Item addItem(ItemDto itemDto, long userId);

    Item updateItem(ItemDto itemDto, long itemId, long userId);

    void deleteById(long itemId);
}
