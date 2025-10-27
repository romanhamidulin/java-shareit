package ru.yandex.practicum.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class BookingAddDto {
    @NotNull
    private String start;
    @NotNull
    private String end;
    @NotNull
    private Long itemId;
}