package ru.yandex.practicum.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.booking.dto.BookingAddDto;
import ru.yandex.practicum.booking.dto.BookingDto;
import ru.yandex.practicum.booking.entity.Booking;
import ru.yandex.practicum.booking.entity.BookingStatus;
import ru.yandex.practicum.booking.repository.BookingRepository;
import ru.yandex.practicum.exception.NotAvailableException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.item.entity.Item;
import ru.yandex.practicum.item.repository.ItemRepository;
import ru.yandex.practicum.user.entity.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты BookingServiceImpl")
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingAddDto bookingAddDto;

    @BeforeEach
    void setUp() {
        booker = User.builder()
                .id(1L)
                .name("Booker")
                .email("booker@example.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("Owner")
                .email("owner@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .ownerId(owner.getId())
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        bookingAddDto = BookingAddDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1).toString())
                .end(LocalDateTime.now().plusDays(3).toString())
                .build();
    }

    @Test
    @DisplayName("Должен получить все бронирования пользователя")
    void getAllBookingsByUserId_ShouldReturnBookings() {
        // given
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findBookingsByBookerId(booker.getId()))
                .thenReturn(List.of(booking));

        // when
        List<BookingDto> result = bookingService.getAllBookingsByUserId(booker.getId());

        // then
        assertThat(result).hasSize(1);
        verify(userRepository).existsById(booker.getId());
        verify(bookingRepository).findBookingsByBookerId(booker.getId());
    }

    @Test
    @DisplayName("Должен выбросить исключение при получении бронирований несуществующего пользователя")
    void getAllBookingsByUserId_ShouldThrowWhenUserNotFound() {
        // given
        Long nonExistentUserId = 999L;
        when(userRepository.existsById(nonExistentUserId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> bookingService.getAllBookingsByUserId(nonExistentUserId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с ID = 999 не найден!");

        verify(userRepository).existsById(nonExistentUserId);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Должен получить бронирование по ID для владельца")
    void getBookingById_ShouldReturnBookingForOwner() {
        // given
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        // when
        BookingDto result = bookingService.getBookingById(booking.getId(), owner.getId());

        // then
        assertThat(result).isNotNull();
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    @DisplayName("Должен получить бронирование по ID для бронирующего")
    void getBookingById_ShouldReturnBookingForBooker() {
        // given
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        // when
        BookingDto result = bookingService.getBookingById(booking.getId(), booker.getId());

        // then
        assertThat(result).isNotNull();
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    @DisplayName("Должен выбросить исключение при получении несуществующего бронирования")
    void getBookingById_ShouldThrowWhenBookingNotFound() {
        // given
        Long nonExistentBookingId = 999L;
        when(bookingRepository.findById(nonExistentBookingId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookingService.getBookingById(nonExistentBookingId, booker.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронирование с ID = 999, не найдено!");

        verify(bookingRepository).findById(nonExistentBookingId);
    }

    @Test
    @DisplayName("Должен выбросить исключение при доступе к бронированию посторонним пользователем")
    void getBookingById_ShouldThrowWhenUnauthorizedAccess() {
        // given
        Long otherUserId = 999L;
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        // when & then
        assertThatThrownBy(() -> bookingService.getBookingById(booking.getId(), otherUserId))
                .isInstanceOf(NotAvailableException.class)
                .hasMessage("Информация о конкретном бронировании доступна только заявителю или владельцу вещи");

        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    @DisplayName("Должен создать бронирование")
    void createBooking_ShouldCreateBooking() {
        // given
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // when
        BookingDto result = bookingService.createBooking(bookingAddDto, booker.getId());

        // then
        assertThat(result).isNotNull();
        verify(userRepository).existsById(booker.getId());
        verify(itemRepository).findById(item.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение при создании бронирования несуществующим пользователем")
    void createBooking_ShouldThrowWhenUserNotFound() {
        // given
        Long nonExistentUserId = 999L;
        when(userRepository.existsById(nonExistentUserId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> bookingService.createBooking(bookingAddDto, nonExistentUserId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователя с таким ID = 999, не существует!");

        verify(userRepository).existsById(nonExistentUserId);
        verifyNoInteractions(itemRepository, bookingRepository);
    }

    @Test
    @DisplayName("Должен выбросить исключение при создании бронирования несуществующего предмета")
    void createBooking_ShouldThrowWhenItemNotFound() {
        // given
        Long nonExistentItemId = 999L;
        bookingAddDto.setItemId(nonExistentItemId);

        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(itemRepository.findById(nonExistentItemId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookingService.createBooking(bookingAddDto, booker.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Предмет с ID = 999, не найден!");

        verify(userRepository).existsById(booker.getId());
        verify(itemRepository).findById(nonExistentItemId);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Должен выбросить исключение при создании бронирования недоступного предмета")
    void createBooking_ShouldThrowWhenItemNotAvailable() {
        // given
        item.setAvailable(false);
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        // when & then
        assertThatThrownBy(() -> bookingService.createBooking(bookingAddDto, booker.getId()))
                .isInstanceOf(NotAvailableException.class)
                .hasMessage("Предмет недоступен для бронирования!");

        verify(userRepository).existsById(booker.getId());
        verify(itemRepository).findById(item.getId());
        verifyNoInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Должен обновить бронирование с APPROVED")
    void updateBooking_ShouldUpdateWithApproved() {
        // given
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // when
        BookingDto result = bookingService.updateBooking(booking.getId(), true, owner.getId());

        // then
        assertThat(result).isNotNull();
        verify(bookingRepository).findById(booking.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Должен обновить бронирование с REJECTED")
    void updateBooking_ShouldUpdateWithRejected() {
        // given
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // when
        BookingDto result = bookingService.updateBooking(booking.getId(), false, owner.getId());

        // then
        assertThat(result).isNotNull();
        verify(bookingRepository).findById(booking.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение при обновлении несуществующего бронирования")
    void updateBooking_ShouldThrowWhenBookingNotFound() {
        // given
        Long nonExistentBookingId = 999L;
        when(bookingRepository.findById(nonExistentBookingId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookingService.updateBooking(nonExistentBookingId, true, owner.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронирование с ID = 999, не найдено!");

        verify(bookingRepository).findById(nonExistentBookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение при обновлении бронирования не владельцем")
    void updateBooking_ShouldThrowWhenNotOwner() {
        // given
        Long otherUserId = 999L;
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        // when & then
        assertThatThrownBy(() -> bookingService.updateBooking(booking.getId(), true, otherUserId))
                .isInstanceOf(NotAvailableException.class)
                .hasMessage("У ID = 999 доступа к подтверждению данного бронирования!");

        verify(bookingRepository).findById(booking.getId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Должен удалить бронирование")
    void deleteBooking_ShouldDeleteBooking() {
        // given
        doNothing().when(bookingRepository).deleteById(booking.getId());

        // when
        bookingService.deleteBooking(booking.getId());

        // then
        verify(bookingRepository).deleteById(booking.getId());
    }
}