package ru.yandex.practicum.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.booking.entity.Booking;
import ru.yandex.practicum.booking.entity.BookingStatus;
import ru.yandex.practicum.booking.repository.BookingRepository;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.repository.CommentRepository;
import ru.yandex.practicum.exception.NotAvailableException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.item.dto.ItemDtoWithDate;
import ru.yandex.practicum.item.entity.Item;
import ru.yandex.practicum.item.repository.ItemRepository;
import ru.yandex.practicum.request.repository.ItemRequestRepository;
import ru.yandex.practicum.user.entity.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты ItemServiceImpl")
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("user@example.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("Item Owner")
                .email("owner@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .ownerId(owner.getId())
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        commentDto = CommentDto.builder()
                .withText("Great item!")
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    @DisplayName("Должен получить все предметы пользователя")
    void getAllUsersItems_ShouldReturnItems() {
        // given
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(item));

        // when
        List<ItemDto> result = itemService.getAllUsersItems(owner.getId());

        // then
        assertThat(result).hasSize(1);
        verify(itemRepository).findAllByOwnerId(owner.getId());
    }

    @Test
    @DisplayName("Должен получить предмет по ID с датами бронирования")
    void getItemById_ShouldReturnItemWithDates() {
        // given
        LocalDateTime lastBookingEnd = LocalDateTime.now().minusDays(1);
        LocalDateTime nextBookingStart = LocalDateTime.now().plusDays(1);

        Booking lastBooking = Booking.builder().end(lastBookingEnd).build();
        Booking nextBooking = Booking.builder().start(nextBookingStart).build();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                eq(item.getId()), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                eq(item.getId()), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.of(nextBooking));

        // when
        ItemDtoWithDate result = itemService.getItemById(item.getId(), user.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getLastBooking()).isEqualTo(lastBookingEnd);
        assertThat(result.getNextBooking()).isEqualTo(nextBookingStart);
        verify(itemRepository).findById(item.getId());
    }

    @Test
    @DisplayName("Должен выбросить исключение при получении несуществующего предмета")
    void getItemById_ShouldThrowWhenItemNotFound() {
        // given
        Long nonExistentItemId = 999L;
        when(itemRepository.findById(nonExistentItemId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> itemService.getItemById(nonExistentItemId, user.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вещь с данным ID не найдена!");

        verify(itemRepository).findById(nonExistentItemId);
    }

    @Test
    @DisplayName("Должен найти предметы по тексту")
    void getItemsByText_ShouldReturnMatchingItems() {
        // given
        String searchText = "test";
        when(itemRepository.findItemsByNameIgnoreCase(searchText)).thenReturn(List.of(item));

        // when
        List<ItemDto> result = itemService.getItemsByText(searchText);

        // then
        assertThat(result).hasSize(1);
        verify(itemRepository).findItemsByNameIgnoreCase(searchText);
    }

    @Test
    @DisplayName("Должен вернуть пустой список при пустом тексте поиска")
    void getItemsByText_ShouldReturnEmptyListForEmptyText() {
        // when
        List<ItemDto> result = itemService.getItemsByText("");

        // then
        assertThat(result).isEmpty();
        verifyNoInteractions(itemRepository);
    }


    @Test
    @DisplayName("Должен выбросить исключение при добавлении комментария к несуществующему предмету")
    void addComment_ShouldThrowWhenItemNotFound() {
        // given
        Long nonExistentItemId = 999L;
        when(itemRepository.findById(nonExistentItemId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> itemService.addComment(commentDto, nonExistentItemId, user.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вещи с ID = 999 не существует!");

        verify(itemRepository).findById(nonExistentItemId);
        verifyNoInteractions(userRepository, bookingRepository, commentRepository);
    }

    @Test
    @DisplayName("Должен выбросить исключение при добавлении комментария несуществующим пользователем")
    void addComment_ShouldThrowWhenUserNotFound() {
        // given
        Long nonExistentUserId = 999L;
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> itemService.addComment(commentDto, item.getId(), nonExistentUserId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователя с ID = 999 - не существует!");

        verify(itemRepository).findById(item.getId());
        verify(userRepository).findById(nonExistentUserId);
        verifyNoInteractions(bookingRepository, commentRepository);
    }

    @Test
    @DisplayName("Должен выбросить исключение при добавлении комментария без бронирования")
    void addComment_ShouldThrowWhenBookingNotFound() {
        // given
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByItemIdAndBookerId(item.getId(), user.getId()))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> itemService.addComment(commentDto, item.getId(), user.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронирование с Item ID = 1 и Booker Id = 1 - не найдено!");

        verify(itemRepository).findById(item.getId());
        verify(userRepository).findById(user.getId());
        verify(bookingRepository).findBookingsByItemIdAndBookerId(item.getId(), user.getId());
        verifyNoInteractions(commentRepository);
    }

    @Test
    @DisplayName("Должен выбросить исключение при добавлении комментария до окончания бронирования")
    void addComment_ShouldThrowWhenBookingNotEnded() {
        // given
        booking.setEnd(LocalDateTime.now().plusDays(1)); // Бронирование еще не закончилось

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByItemIdAndBookerId(item.getId(), user.getId()))
                .thenReturn(Optional.of(booking));

        // when & then
        assertThatThrownBy(() -> itemService.addComment(commentDto, item.getId(), user.getId()))
                .isInstanceOf(NotAvailableException.class)
                .hasMessage("Комментарий нельзя поставить, так как бронирование вещи не окончено!");

        verify(itemRepository).findById(item.getId());
        verify(userRepository).findById(user.getId());
        verify(bookingRepository).findBookingsByItemIdAndBookerId(item.getId(), user.getId());
        verifyNoInteractions(commentRepository);
    }

    @Test
    @DisplayName("Должен удалить предмет")
    void deleteById_ShouldDeleteItem() {
        // given
        doNothing().when(itemRepository).deleteById(item.getId());

        // when
        itemService.deleteById(item.getId());

        // then
        verify(itemRepository).deleteById(item.getId());
    }
}