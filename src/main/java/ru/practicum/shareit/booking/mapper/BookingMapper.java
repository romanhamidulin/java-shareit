package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.entity.Booking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingMapper {
    public static final DateTimeFormatter formatter =
            DateTimeFormatter.ISO_DATE_TIME;

    public static BookingDto entityItemToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart().format(formatter))
                .end(booking.getEnd().format(formatter))
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Booking dtoToEntityItem(BookingDto dto) {
        return Booking.builder()
                .start(LocalDateTime.parse(dto.getStart()))
                .end(LocalDateTime.parse(dto.getEnd()))
                .item(dto.getItem())
                .build();
    }

    public static Booking dtoToEntityItem(BookingAddDto dto) {
        return Booking.builder()
                .start(LocalDateTime.parse(dto.getStart()))
                .end(LocalDateTime.parse(dto.getEnd()))
                .build();
    }
}
