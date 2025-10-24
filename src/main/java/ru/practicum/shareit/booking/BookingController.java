package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestHeader(name = REQUEST_HEADER) Long userId) {
        return bookingService.getAllBookingsByUserId(userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @Valid @Positive @PathVariable Long bookingId,
            @RequestHeader(name = REQUEST_HEADER) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PostMapping
    public BookingDto createNewBooking(
            @Valid @RequestBody BookingAddDto bookingDto,
            @Valid @Positive @RequestHeader(REQUEST_HEADER) Long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(name = REQUEST_HEADER) Long userId) {
        return bookingService.updateBooking(bookingId, approved, userId);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable Long bookingId) {
        bookingService.deleteBooking(bookingId);
    }
}