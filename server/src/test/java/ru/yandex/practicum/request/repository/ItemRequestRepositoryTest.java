package ru.yandex.practicum.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.yandex.practicum.request.entity.ItemRequest;
import ru.yandex.practicum.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void shouldSaveItemRequest() {
        // Given
        User requester = User.builder()
                .name("Requester")
                .email("requester@example.com")
                .build();
        User savedRequester = entityManager.persistAndFlush(requester);

        ItemRequest request = ItemRequest.builder()
                .description("Need a laptop for work")
                .requester(savedRequester)
                .created(LocalDateTime.now())
                .build();

        // When
        ItemRequest saved = itemRequestRepository.save(request);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Need a laptop for work", saved.getDescription());
        assertEquals(savedRequester.getId(), saved.getRequester().getId());
        assertNotNull(saved.getCreated());

        // Verify with EntityManager
        ItemRequest found = entityManager.find(ItemRequest.class, saved.getId());
        assertEquals("Need a laptop for work", found.getDescription());
    }

    @Test
    void shouldFindAllByRequesterIdOrderByCreatedDesc() {
        // Given
        User requester = User.builder()
                .name("Test Requester")
                .email("test@example.com")
                .build();
        User savedRequester = entityManager.persistAndFlush(requester);

        ItemRequest request1 = ItemRequest.builder()
                .description("Request 1")
                .requester(savedRequester)
                .created(LocalDateTime.now().minusDays(1))
                .build();

        ItemRequest request2 = ItemRequest.builder()
                .description("Request 2")
                .requester(savedRequester)
                .created(LocalDateTime.now())
                .build();

        entityManager.persistAndFlush(request1);
        entityManager.persistAndFlush(request2);

        // When
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(savedRequester.getId());

        // Then
        assertNotNull(requests);
        assertEquals(2, requests.size());
        // Проверяем сортировку по убыванию даты (последний созданный первый)
        assertEquals("Request 2", requests.get(0).getDescription());
        assertEquals("Request 1", requests.get(1).getDescription());
    }

    @Test
    void shouldFindAllByRequesterIdNotOrderByCreatedDesc() {
        // Given
        User requester1 = User.builder().name("Requester 1").email("req1@example.com").build();
        User requester2 = User.builder().name("Requester 2").email("req2@example.com").build();
        User savedRequester1 = entityManager.persistAndFlush(requester1);
        User savedRequester2 = entityManager.persistAndFlush(requester2);

        ItemRequest request1 = ItemRequest.builder()
                .description("Request from user 1")
                .requester(savedRequester1)
                .created(LocalDateTime.now().minusDays(1))
                .build();

        ItemRequest request2 = ItemRequest.builder()
                .description("Request from user 2")
                .requester(savedRequester2)
                .created(LocalDateTime.now())
                .build();

        entityManager.persistAndFlush(request1);
        entityManager.persistAndFlush(request2);

        // When - ищем запросы НЕ от requester1
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(savedRequester1.getId());

        // Then
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals("Request from user 2", requests.get(0).getDescription());
        assertEquals(savedRequester2.getId(), requests.get(0).getRequester().getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoRequestsForRequester() {
        // Given
        User requester = User.builder()
                .name("User without requests")
                .email("norequests@example.com")
                .build();
        User savedRequester = entityManager.persistAndFlush(requester);

        // When
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(savedRequester.getId());

        // Then
        assertNotNull(requests);
        assertTrue(requests.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoOtherRequests() {
        // Given
        User requester = User.builder()
                .name("Only user")
                .email("only@example.com")
                .build();
        User savedRequester = entityManager.persistAndFlush(requester);

        // When - ищем запросы не от этого пользователя
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(savedRequester.getId());

        // Then
        assertNotNull(requests);
        assertTrue(requests.isEmpty());
    }

    @Test
    void shouldHandleMultipleUsersWithRequests() {
        // Given
        User user1 = User.builder().name("User 1").email("user1@example.com").build();
        User user2 = User.builder().name("User 2").email("user2@example.com").build();
        User user3 = User.builder().name("User 3").email("user3@example.com").build();

        User savedUser1 = entityManager.persistAndFlush(user1);
        User savedUser2 = entityManager.persistAndFlush(user2);
        User savedUser3 = entityManager.persistAndFlush(user3);

        // Создаем запросы для всех пользователей
        ItemRequest request1 = ItemRequest.builder().description("Req 1").requester(savedUser1).created(LocalDateTime.now().minusDays(2)).build();
        ItemRequest request2 = ItemRequest.builder().description("Req 2").requester(savedUser2).created(LocalDateTime.now().minusDays(1)).build();
        ItemRequest request3 = ItemRequest.builder().description("Req 3").requester(savedUser3).created(LocalDateTime.now()).build();

        entityManager.persistAndFlush(request1);
        entityManager.persistAndFlush(request2);
        entityManager.persistAndFlush(request3);

        // When - ищем запросы не от user2
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(savedUser2.getId());

        // Then
        assertNotNull(requests);
        assertEquals(2, requests.size());
        // Проверяем сортировку по убыванию даты
        assertEquals("Req 3", requests.get(0).getDescription()); // самый новый
        assertEquals("Req 1", requests.get(1).getDescription()); // самый старый
    }
}