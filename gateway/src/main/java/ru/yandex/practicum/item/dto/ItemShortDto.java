package ru.yandex.practicum.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ItemShortDto {
    private Long id;
    private String name;
    private Long ownerId;
}
