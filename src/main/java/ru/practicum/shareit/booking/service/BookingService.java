package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    List<BookingDto> getAllBooking();

    BookingDto getById();

    BookingDto addBooking(BookingDto bookingDto);

    BookingDto updateBooking(BookingDto bookingDto);

    void deleteById(Long bookingId);
}
