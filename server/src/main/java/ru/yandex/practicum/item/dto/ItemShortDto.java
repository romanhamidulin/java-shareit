package ru.yandex.practicum.item.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
public class ItemShortDto {
    private Long id;
    private String name;
    private Long ownerId;
}
