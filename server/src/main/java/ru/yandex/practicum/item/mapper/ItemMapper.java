package ru.yandex.practicum.item.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.comment.mapper.CommentMapper;
import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.item.dto.ItemDtoWithDate;
import ru.yandex.practicum.item.dto.ItemShortDto;
import ru.yandex.practicum.item.entity.Item;

import java.time.LocalDateTime;

@UtilityClass
public class ItemMapper {
    public ItemDto entityItemToDto(Item item) {
        ItemDto result = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getComments() != null) {
            result.setComments(item.getComments().stream().map(CommentMapper::entityItemToDto).toList());
            return result;
        }
        return result;
    }

    public ItemDtoWithDate entityItemToDtoWithDate(Item item, LocalDateTime last, LocalDateTime next) {
        ItemDtoWithDate result = ItemDtoWithDate.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getComments() != null) {
            result.setComments(item.getComments().stream().map(CommentMapper::entityItemToDto).toList());
        }
        if (last != null && next != null) {
            result.setLastBooking(last);
            result.setNextBooking(next);
        }
        return result;
    }

    public Item dtoToEntityItem(ItemDto itemDto, long ownerId) {
        return Item.builder()
                .id(itemDto.getId())
                .ownerId(ownerId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemShortDto entityToShortDto(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwnerId())
                .build();
    }
}
