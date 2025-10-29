package ru.yandex.practicum.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.booking.dto.BookingAddDto;
import ru.yandex.practicum.booking.dto.BookingDto;
import ru.yandex.practicum.booking.entity.Booking;
import ru.yandex.practicum.item.entity.Item;
import ru.yandex.practicum.item.mapper.ItemMapper;
import ru.yandex.practicum.user.mapper.UserMapper;

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
                .item(ItemMapper.entityItemToDto(booking.getItem()))
                .booker(UserMapper.entityUserToDto(booking.getBooker()))
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
