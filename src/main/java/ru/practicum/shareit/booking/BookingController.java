package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> getAllBooking() {
        return bookingService.getAllBooking();
    }

    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(bookingDto);
    }

    @PutMapping
    public BookingDto updateBooking(@RequestBody BookingDto bookingDto) {
        return bookingService.updateBooking(bookingDto);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable Long bookingId) {
        bookingService.deleteById(bookingId);
    }
}
