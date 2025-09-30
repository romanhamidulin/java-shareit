package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    @Override
    public List<BookingDto> getAllBooking() {
        return null;
    }

    @Override
    public BookingDto getById() {
        return null;
    }

    @Override
    public BookingDto addBooking(BookingDto bookingDto) {
        return null;
    }

    @Override
    public BookingDto updateBooking(BookingDto bookingDto) {
        return null;
    }

    @Override
    public void deleteById(Long bookingId) {

    }
}
