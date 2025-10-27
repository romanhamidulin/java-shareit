package ru.yandex.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.item.mapper.ItemMapper;
import ru.yandex.practicum.request.dto.ItemRequestDto;
import ru.yandex.practicum.request.entity.ItemRequest;
import ru.yandex.practicum.user.entity.User;
import ru.yandex.practicum.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequestDto toDto(ItemRequest entity) {
        return ItemRequestDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .created(entity.getCreated())
                .items(entity.getItems().stream().map(ItemMapper::entityToShortDto).toList())
                .requester(UserMapper.entityUserToDto(entity.getRequester()))
                .build();
    }

    public ItemRequestDto toDto(ItemRequest entity, List<ItemDto> items) {
        return ItemRequestDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .created(entity.getCreated())
                .requester(UserMapper.entityUserToDto(entity.getRequester()))
                .build();
    }

    public ItemRequest toEntity(ItemRequestDto dto, User user) {
        return ItemRequest.builder()
                .title(dto.getTitle())
                .requester(user)
                .description(dto.getDescription())
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
    }
}
