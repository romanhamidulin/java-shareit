package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    List<BookingDto> getAllBookingsByUserId(Long userId);

    BookingDto getBookingById(Long bookingId, Long userId);

    BookingDto createBooking(BookingAddDto bookingDto, Long userId);

    BookingDto updateBooking(Long bookingId, Boolean isApproved, Long userId);

    void deleteBooking(Long bookingId);
}
