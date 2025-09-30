package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingRepository {
    List<BookingDto> getAllBooking();

    BookingDto getById();

    BookingDto addBooking();

    BookingDto updateBooking();

    void deleteById();
}
