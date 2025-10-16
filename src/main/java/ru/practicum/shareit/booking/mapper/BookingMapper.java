package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.item.entity.Item;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class BookingMapper {
    public static final DateTimeFormatter formatter =
            DateTimeFormatter.ISO_DATE_TIME;

    public BookingDto entityItemToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart().format(formatter))
                .end(booking.getEnd().format(formatter))
                .itemId(booking.getItem() != null ? booking.getItem().getId() : null)
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .status(booking.getStatus())
                .build();
    }

    public Booking dtoToEntityItem(BookingDto dto, Item item) {
        return Booking.builder()
                .start(LocalDateTime.parse(dto.getStart()))
                .end(LocalDateTime.parse(dto.getEnd()))
                .item(item)
                .build();
    }

    public Booking dtoToEntityItem(BookingAddDto dto) {
        return Booking.builder()
                .start(LocalDateTime.parse(dto.getStart()))
                .end(LocalDateTime.parse(dto.getEnd()))
                .build();
    }
}
