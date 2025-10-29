package ru.yandex.practicum.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BookingAddDto {
    @NotNull
    private String start;
    @NotNull
    private String end;
    @NotNull
    private Long itemId;
}