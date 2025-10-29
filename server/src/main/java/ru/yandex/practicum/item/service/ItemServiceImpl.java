package ru.yandex.practicum.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.booking.entity.Booking;
import ru.yandex.practicum.booking.entity.BookingStatus;
import ru.yandex.practicum.booking.repository.BookingRepository;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.entity.Comment;
import ru.yandex.practicum.comment.mapper.CommentMapper;
import ru.yandex.practicum.comment.repository.CommentRepository;
import ru.yandex.practicum.exception.NotAvailableException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.item.dto.ItemDtoWithDate;
import ru.yandex.practicum.item.entity.Item;
import ru.yandex.practicum.item.mapper.ItemMapper;
import ru.yandex.practicum.item.repository.ItemRepository;
import ru.yandex.practicum.request.entity.ItemRequest;
import ru.yandex.practicum.request.repository.ItemRequestRepository;
import ru.yandex.practicum.user.entity.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public List<ItemDto> getAllUsersItems(Long userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::entityItemToDto)
                .toList();
    }

    @Override
    public ItemDtoWithDate getItemById(Long itemId, Long userId) {
        Item itemEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с данным ID не найдена!"));
        Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        return ItemMapper.entityItemToDtoWithDate(
                itemEntity,
                lastBooking.map(Booking::getEnd).orElse(null),
                nextBooking.map(Booking::getStart).orElse(null)
        );
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text.isEmpty()) return new ArrayList<>();
        return itemRepository.findItemsByNameIgnoreCase(text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::entityItemToDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        ItemRequest itemRequest;
        Long requestId = itemDto.getRequestId();
        if (userRepository.existsById(userId)) {
            Item itemToPost = ItemMapper.dtoToEntityItem(itemDto, userId);
            if (requestId != null) {
                if (itemDto.getName() == null)
                    throw new NotAvailableException("Нельзя ответить вещью без названия на запрос");
                itemRequest = requestRepository.findById(requestId).orElseThrow(
                        () -> new NotFoundException("Запрос с ID = %d не найден!".formatted(requestId))
                );
                itemToPost.setRequest(itemRequest);
            }
            return ItemMapper.entityItemToDto(itemRepository.save(itemToPost));
        } else {
            throw new NotFoundException("Пользователя с ID %d - не существует!".formatted(userId));
        }
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Вещи с ID = %d не существует!", itemId)));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователя с ID = %d - не существует!", userId)));
        Booking booking = bookingRepository.findBookingsByItemIdAndBookerId(itemId, userId).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Бронирование с Item ID = %d и Booker Id = %d - не найдено!", itemId, userId)));
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            return CommentMapper.entityItemToDto(commentRepository.save(Comment.builder()
                    .withUser(user)
                    .withItem(item)
                    .withText(commentDto.getText())
                    .withCreated(LocalDateTime.now())
                    .build()
            ));
        } else {
            throw new NotAvailableException("Комментарий нельзя поставить, так как бронирование вещи не окончено!");
        }
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findItemByIdAndOwnerId(itemId, userId).orElseThrow(
                () -> new NotFoundException("Вещи с ID %d - не существует!".formatted(itemId)));
        log.info("Найден Item для update -> {}", item);

        String nameDto = itemDto.getName();
        if (nameDto != null) {
            item.setName(nameDto);
        }
        String descriptionDto = itemDto.getDescription();
        if (descriptionDto != null) {
            item.setDescription(descriptionDto);
        }
        Boolean available = itemDto.getAvailable();
        if (available != null) {
            item.setAvailable(available);
        }

        return ItemMapper.entityItemToDto(itemRepository.save(item));
    }

    @Override
    public void deleteById(long itemId) {
        itemRepository.deleteById(itemId);
    }
}

