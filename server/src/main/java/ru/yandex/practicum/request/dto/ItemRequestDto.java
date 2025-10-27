package ru.yandex.practicum.request.dto;

import lombok.*;
import ru.yandex.practicum.item.dto.ItemShortDto;
import ru.yandex.practicum.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private String title;
    private LocalDateTime created;
    private UserDto requester;
    private List<ItemShortDto> items;
}
