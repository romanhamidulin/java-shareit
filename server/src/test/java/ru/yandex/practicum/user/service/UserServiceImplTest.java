package ru.yandex.practicum.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.exception.ExistException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.entity.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final Long userId = 1L;

    @Test
    void getAllUsers_whenNoUsers_shouldReturnEmptyList() {
        // Given
        when(userRepository.findAll()).thenReturn(List.of());

        // When
        List<UserDto> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void addUser_whenEmailIsNull_shouldThrowValidationException() {
        // Given
        UserDto userDto = UserDto.builder()
                .name("User Without Email")
                .email(null)
                .build();

        // When & Then
        assertThrows(ValidationException.class, () -> userService.addUser(userDto));

        verify(userRepository, never()).existsUserByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void addUser_whenEmailAlreadyExists_shouldThrowExistException() {
        // Given
        UserDto userDto = UserDto.builder()
                .name("Existing User")
                .email("existing@example.com")
                .build();

        when(userRepository.existsUserByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThrows(ExistException.class, () -> userService.addUser(userDto));

        verify(userRepository).existsUserByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_whenUserNotFound_shouldThrowNotFoundException() {
        // Given
        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> userService.updateUser(updateDto, userId));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).existsUserByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_whenEmailAlreadyExists_shouldThrowExistException() {
        // Given
        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("existing@example.com")
                .build();

        User existingUser = User.builder()
                .id(userId)
                .name("Original Name")
                .email("original@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsUserByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThrows(ExistException.class, () -> userService.updateUser(updateDto, userId));

        verify(userRepository).findById(userId);
        verify(userRepository).existsUserByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteById_shouldDeleteUser() {
        // Given
        doNothing().when(userRepository).deleteById(userId);

        // When
        userService.deleteById(userId);

        // Then
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteById_withNonExistentUser_shouldNotThrowException() {
        // Given
        doNothing().when(userRepository).deleteById(userId);

        // When & Then - should not throw exception even if user doesn't exist
        assertDoesNotThrow(() -> userService.deleteById(userId));

        verify(userRepository).deleteById(userId);
    }
}