package ru.yandex.practicum.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.booking.entity.Booking;
import ru.yandex.practicum.booking.entity.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByBookerId(Long userId);

    Optional<Booking> findBookingsByItemIdAndBookerId(Long itemId, Long bookerId);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
            Long itemId, LocalDateTime now, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
            Long itemId, LocalDateTime now, BookingStatus status);
}
