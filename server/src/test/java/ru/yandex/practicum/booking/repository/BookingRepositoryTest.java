package ru.yandex.practicum.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.yandex.practicum.booking.entity.Booking;
import ru.yandex.practicum.booking.entity.BookingStatus;
import ru.yandex.practicum.item.entity.Item;
import ru.yandex.practicum.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private User createUser(String name, String email) {
        return User.builder()
                .name(name)
                .email(email)
                .build();
    }

    private Item createItem(User owner, String name, String description) {
        return Item.builder()
                .name(name)
                .description(description)
                .available(true)
                .ownerId(owner.getId())
                .build();
    }

    private Booking createBooking(Item item, User booker, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .start(start)
                .end(end)
                .status(status)
                .build();
    }

    @Test
    void shouldSaveBooking() {
        // Given
        User owner = createUser("Owner", "owner@example.com");
        User booker = createUser("Booker", "booker@example.com");
        User savedOwner = entityManager.persistAndFlush(owner);
        User savedBooker = entityManager.persistAndFlush(booker);

        Item item = createItem(savedOwner, "Test Item", "Test Description");
        Item savedItem = entityManager.persistAndFlush(item);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = createBooking(savedItem, savedBooker, start, end, BookingStatus.WAITING);

        // When
        Booking saved = bookingRepository.save(booking);

        // Then
        assertNotNull(saved.getId());
        assertEquals(savedItem.getId(), saved.getItem().getId());
        assertEquals(savedBooker.getId(), saved.getBooker().getId());
        assertEquals(start, saved.getStart());
        assertEquals(end, saved.getEnd());
        assertEquals(BookingStatus.WAITING, saved.getStatus());
    }

    @Test
    void shouldFindBookingsByBookerId() {
        // Given
        User owner = createUser("Owner", "owner@example.com");
        User booker = createUser("Booker", "booker@example.com");
        User savedOwner = entityManager.persistAndFlush(owner);
        User savedBooker = entityManager.persistAndFlush(booker);

        Item item1 = createItem(savedOwner, "Item 1", "Description 1");
        Item item2 = createItem(savedOwner, "Item 2", "Description 2");
        Item savedItem1 = entityManager.persistAndFlush(item1);
        Item savedItem2 = entityManager.persistAndFlush(item2);

        Booking booking1 = createBooking(savedItem1, savedBooker,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);
        Booking booking2 = createBooking(savedItem2, savedBooker,
                LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), BookingStatus.APPROVED);

        entityManager.persistAndFlush(booking1);
        entityManager.persistAndFlush(booking2);

        // When
        List<Booking> bookings = bookingRepository.findBookingsByBookerId(savedBooker.getId());

        // Then
        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertTrue(bookings.stream().allMatch(b -> b.getBooker().getId().equals(savedBooker.getId())));
    }

    @Test
    void shouldFindBookingsByItemIdAndBookerId() {
        // Given
        User owner = createUser("Owner", "owner@example.com");
        User booker = createUser("Booker", "booker@example.com");
        User savedOwner = entityManager.persistAndFlush(owner);
        User savedBooker = entityManager.persistAndFlush(booker);

        Item item = createItem(savedOwner, "Specific Item", "Specific Description");
        Item savedItem = entityManager.persistAndFlush(item);

        Booking booking = createBooking(savedItem, savedBooker,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);
        Booking savedBooking = entityManager.persistAndFlush(booking);

        // When
        Optional<Booking> found = bookingRepository.findBookingsByItemIdAndBookerId(
                savedItem.getId(), savedBooker.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(savedBooking.getId(), found.get().getId());
        assertEquals(savedItem.getId(), found.get().getItem().getId());
        assertEquals(savedBooker.getId(), found.get().getBooker().getId());
    }

    @Test
    void shouldFindFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc() {
        // Given
        User owner = createUser("Owner", "owner@example.com");
        User booker = createUser("Booker", "booker@example.com");
        User savedOwner = entityManager.persistAndFlush(owner);
        User savedBooker = entityManager.persistAndFlush(booker);

        Item item = createItem(savedOwner, "Test Item", "Test Description");
        Item savedItem = entityManager.persistAndFlush(item);

        LocalDateTime now = LocalDateTime.now();

        // Прошлые бронирования
        Booking pastBooking1 = createBooking(savedItem, savedBooker,
                now.minusDays(3), now.minusDays(1), BookingStatus.APPROVED);
        Booking pastBooking2 = createBooking(savedItem, savedBooker,
                now.minusDays(5), now.minusDays(2), BookingStatus.APPROVED); // заканчивается позже

        entityManager.persistAndFlush(pastBooking1);
        entityManager.persistAndFlush(pastBooking2);

        // When - ищем последнюю завершенную бронь
        Optional<Booking> found = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                savedItem.getId(), now, BookingStatus.APPROVED);

        // Then - должна вернуться бронь с самым поздним end
        assertTrue(found.isPresent());
        assertEquals(pastBooking1.getEnd(), found.get().getEnd()); // pastBooking1 заканчивается позже
    }

    @Test
    void shouldFindFirstByItemIdAndStartAfterAndStatusOrderByStartAsc() {
        // Given
        User owner = createUser("Owner", "owner@example.com");
        User booker = createUser("Booker", "booker@example.com");
        User savedOwner = entityManager.persistAndFlush(owner);
        User savedBooker = entityManager.persistAndFlush(booker);

        Item item = createItem(savedOwner, "Test Item", "Test Description");
        Item savedItem = entityManager.persistAndFlush(item);

        LocalDateTime now = LocalDateTime.now();

        // Будущие бронирования
        Booking futureBooking1 = createBooking(savedItem, savedBooker,
                now.plusDays(3), now.plusDays(4), BookingStatus.APPROVED);
        Booking futureBooking2 = createBooking(savedItem, savedBooker,
                now.plusDays(1), now.plusDays(2), BookingStatus.APPROVED); // начинается раньше

        entityManager.persistAndFlush(futureBooking1);
        entityManager.persistAndFlush(futureBooking2);

        // When - ищем ближайшую будущую бронь
        Optional<Booking> found = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                savedItem.getId(), now, BookingStatus.APPROVED);

        // Then - должна вернуться бронь с самым ранним start
        assertTrue(found.isPresent());
        assertEquals(futureBooking2.getStart(), found.get().getStart()); // futureBooking2 начинается раньше
    }
}