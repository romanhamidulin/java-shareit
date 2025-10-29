package ru.yandex.practicum.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.booking.dto.BookingAddDto;

@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader(name = REQUEST_HEADER) @Positive Long userId) {
        return bookingClient.getBookingsByUser(userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @PathVariable @Positive Long bookingId,
            @RequestHeader(name = REQUEST_HEADER) @Positive Long userId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUBookings(@RequestHeader(name = REQUEST_HEADER) @Positive Long userId) {
        return bookingClient.getBookingsByUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createNewBooking(
            @RequestBody @Valid BookingAddDto bookingAddDto,
            @RequestHeader(REQUEST_HEADER) @Positive Long userId) {
        return bookingClient.createBooking(userId, bookingAddDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(
            @PathVariable @Positive Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(name = REQUEST_HEADER) @Positive Long userId) {
        return bookingClient.updateBooking(userId, bookingId, approved);
    }
}

