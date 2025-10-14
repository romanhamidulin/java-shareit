package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<BookingDto> getAllBookingsByUserId(Long userId) {
        return bookingRepository.findBookingsByBookerId(userId).stream()
                .map(BookingMapper::entityItemToDto)
                .toList();
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Бронирование с ID = %d, не найдено!", bookingId)));
        if (userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwnerId())) {
            return BookingMapper.entityItemToDto(booking);
        } else {
            throw new NotAvailableException(
                    "Информация о конкретном бронировании доступна только заявителю или владельцу вещи");
        }
    }

    @Override
    public BookingDto createBooking(BookingAddDto bookingDto, Long userId) {
        // TODO: Дописать логику, где бизнес-ошибка с start, end.
        if (userRepository.existsById(userId)) {
            Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                    () -> new NotFoundException(String.format("Предмет с ID = %d, не найден!", bookingDto.getItemId())));
            if (item.getAvailable()) {
                Booking booking = BookingMapper.dtoToEntityItem(bookingDto);
                User booker = new User();
                booker.setId(userId);
                booking.setBooker(booker);
                booking.setItem(item);
                booking.setStatus(BookingStatus.WAITING);
                var result = BookingMapper.entityItemToDto(bookingRepository.save(booking));
                log.info("Бронирование создано! {}", result);
                return result;
            } else {
                throw new NotAvailableException("Предмет недоступен для бронирования!");
            }
        } else {
            throw new NotFoundException(String.format("Пользователя с таким ID = %d, не существует!", userId));
        }
    }

    @Override
    public BookingDto updateBooking(Long bookingId, Boolean isApproved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с ID = %d, не найдено!", bookingId)));
        if (booking.getItem().getOwnerId().equals(userId)) {
            if (isApproved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            return BookingMapper.entityItemToDto(bookingRepository.save(booking));
        } else {
            throw new NotAvailableException(
                    String.format("У ID = %d доступа к подтверждению данного бронирования!", userId));
        }
    }

    @Override
    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }
}