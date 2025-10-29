package ru.yandex.practicum.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.ExistException;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.entity.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void addUser_shouldPersistUserInDatabase() {
        // Given
        UserDto userDto = UserDto.builder()
                .name("Integration User")
                .email("integration@example.com")
                .build();

        // When
        UserDto result = userService.addUser(userDto);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Integration User", result.getName());
        assertEquals("integration@example.com", result.getEmail());

        // Verify in database
        User savedUser = userRepository.findById(result.getId()).orElseThrow();
        assertEquals("Integration User", savedUser.getName());
        assertEquals("integration@example.com", savedUser.getEmail());
    }

    @Test
    void addUser_withDuplicateEmail_shouldThrowException() {
        // Given
        UserDto userDto1 = UserDto.builder()
                .name("User One")
                .email("duplicate@example.com")
                .build();

        UserDto userDto2 = UserDto.builder()
                .name("User Two")
                .email("duplicate@example.com")
                .build();

        // When
        userService.addUser(userDto1);

        // Then
        assertThrows(ExistException.class, () -> userService.addUser(userDto2));
    }

    @Test
    void updateUser_shouldUpdateUserInDatabase() {
        // Given
        User existingUser = User.builder()
                .name("Original Name")
                .email("original@example.com")
                .build();
        User savedUser = userRepository.save(existingUser);

        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        // When
        UserDto result = userService.updateUser(updateDto, savedUser.getId());

        // Then
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());

        // Verify in database
        User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void getAllUsers_shouldReturnAllUsersFromDatabase() {
        // Given
        userRepository.save(User.builder().name("User 1").email("user1@example.com").build());
        userRepository.save(User.builder().name("User 2").email("user2@example.com").build());

        // When
        List<UserDto> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }
}