package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class BookingDto {
    @NotNull
    private Long id;
    @NotNull
    private String start;
    @NotNull
    private String end;
    @NotNull
    private ItemDto item;
    @NotNull
    private BookingStatus status;
    private UserDto booker;
}
