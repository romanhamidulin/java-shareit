package ru.yandex.practicum.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.booking.entity.BookingStatus;
import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.user.dto.UserDto;

@AllArgsConstructor
@NoArgsConstructor
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
