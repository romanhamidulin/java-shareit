package ru.yandex.practicum.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.booking.dto.BookingAddDto;
import ru.yandex.practicum.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String ENDPOINT = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String host, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(host + ENDPOINT)).build());
    }

    public ResponseEntity<Object> getBookingById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingsByUser(long userId) {
        return get("/owner", userId);
    }

    public ResponseEntity<Object> createBooking(long userId, BookingAddDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> updateBooking(long userId, Long bookingId, Boolean approved) {
        Map<String, Object> params = Map.of(
                "bookingId", bookingId,
                "approved", approved
        );
        return patch("/{bookingId}?approved={approved}", userId, params, null);
    }
}
