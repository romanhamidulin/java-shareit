package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.entity.BookingStatus;

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
    private Long itemId;
    @NotNull
    private BookingStatus status;
    private Long bookerId;
}
