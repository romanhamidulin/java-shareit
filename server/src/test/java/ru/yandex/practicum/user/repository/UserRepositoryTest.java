package ru.yandex.practicum.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.yandex.practicum.user.entity.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUser() {
        // Given
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        // When
        User saved = userRepository.save(user);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Test User", saved.getName());
        assertEquals("test@example.com", saved.getEmail());

        // Verify with EntityManager
        User found = entityManager.find(User.class, saved.getId());
        assertEquals("Test User", found.getName());
    }

    @Test
    void shouldFindUserById() {
        // Given
        User user = User.builder()
                .name("Find User")
                .email("find@example.com")
                .build();
        User saved = entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("Find User", found.get().getName());
        assertEquals("find@example.com", found.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // Given
        Long nonExistentId = 999L;

        // When
        Optional<User> found = userRepository.findById(nonExistentId);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindAllUsers() {
        // Given
        User user1 = User.builder().name("User 1").email("user1@example.com").build();
        User user2 = User.builder().name("User 2").email("user2@example.com").build();

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertNotNull(users);
        assertTrue(users.size() >= 2);
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("User 1")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("User 2")));
    }

    @Test
    void shouldCheckEmailExists() {
        // Given
        User user = User.builder()
                .name("Email Test")
                .email("unique@example.com")
                .build();
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsUserByEmail("unique@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void shouldCheckEmailDoesNotExist() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";

        // When
        boolean exists = userRepository.existsUserByEmail(nonExistentEmail);

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldCheckEmailExistsCaseSensitive() {
        // Given
        User user = User.builder()
                .name("Case Test")
                .email("case@example.com")
                .build();
        entityManager.persistAndFlush(user);

        // When - different case
        boolean exists = userRepository.existsUserByEmail("CASE@example.com");

        // Then - should be case sensitive (depends on database collation)
        // This test might pass or fail depending on your database configuration
        // For most databases, email checks are case sensitive
        assertFalse(exists, "Email check should be case sensitive");
    }

    @Test
    void shouldUpdateUser() {
        // Given
        User user = User.builder()
                .name("Original Name")
                .email("original@example.com")
                .build();
        User saved = entityManager.persistAndFlush(user);

        // When
        saved.setName("Updated Name");
        saved.setEmail("updated@example.com");
        User updated = userRepository.save(saved);

        // Then
        assertEquals(saved.getId(), updated.getId());
        assertEquals("Updated Name", updated.getName());
        assertEquals("updated@example.com", updated.getEmail());

        // Verify in database
        User fromDb = entityManager.find(User.class, saved.getId());
        assertEquals("Updated Name", fromDb.getName());
    }

    @Test
    void shouldDeleteUser() {
        // Given
        User user = User.builder()
                .name("To Delete")
                .email("delete@example.com")
                .build();
        User saved = entityManager.persistAndFlush(user);

        // When
        userRepository.deleteById(saved.getId());

        // Then
        User deleted = entityManager.find(User.class, saved.getId());
        assertNull(deleted);
    }

    @Test
    void shouldHandleEmptyDatabase() {
        // When
        List<User> users = userRepository.findAll();

        // Then
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void shouldSaveUserWithMinimalData() {
        // Given
        User user = User.builder()
                .name("A") // минимальная длина
                .email("a@b.c") // минимальный email
                .build();

        // When
        User saved = userRepository.save(user);

        // Then
        assertNotNull(saved.getId());
        assertEquals("A", saved.getName());
        assertEquals("a@b.c", saved.getEmail());
    }

    @Test
    void shouldSaveUserWithMaxLengthName() {
        // Given
        String maxLengthName = "A".repeat(30); // максимальная длина согласно DTO
        User user = User.builder()
                .name(maxLengthName)
                .email("max@example.com")
                .build();

        // When
        User saved = userRepository.save(user);

        // Then
        assertNotNull(saved.getId());
        assertEquals(maxLengthName, saved.getName());
    }
}