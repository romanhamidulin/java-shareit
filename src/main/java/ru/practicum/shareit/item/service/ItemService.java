package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllUsersItems(Long userId);

    ItemDtoWithDate getItemById(Long itemId, Long userId);

    List<ItemDto> getItemsByText(String text);

    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userid);

    void deleteById(long itemId);
}
