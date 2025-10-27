package ru.yandex.practicum.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.yandex.practicum.item.entity.Item;
import ru.yandex.practicum.request.entity.ItemRequest;
import ru.yandex.practicum.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void shouldSaveItem() {
        // Given
        User ownerId = User.builder()
                .name("ownerId")
                .email("ownerId@example.com")
                .build();
        User savedownerId = entityManager.persistAndFlush(ownerId);

        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .ownerId(savedownerId.getId())
                .build();

        // When
        Item saved = itemRepository.save(item);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Test Item", saved.getName());
        assertEquals("Test Description", saved.getDescription());
        assertTrue(saved.getAvailable());
        assertEquals(savedownerId.getId(), saved.getOwnerId());

        // Verify with EntityManager
        Item found = entityManager.find(Item.class, saved.getId());
        assertEquals("Test Item", found.getName());
    }

    @Test
    void shouldFindAllByownerIdId() {
        // Given
        User ownerId = User.builder()
                .name("Test ownerId")
                .email("ownerId@example.com")
                .build();
        User savedownerId = entityManager.persistAndFlush(ownerId);

        Item item1 = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .ownerId(savedownerId.getId())
                .build();

        Item item2 = Item.builder()
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .ownerId(savedownerId.getId())
                .build();

        entityManager.persistAndFlush(item1);
        entityManager.persistAndFlush(item2);

        // When
        List<Item> items = itemRepository.findAllByOwnerId(savedownerId.getId());

        // Then
        assertNotNull(items);
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item -> item.getName().equals("Item 1")));
        assertTrue(items.stream().anyMatch(item -> item.getName().equals("Item 2")));
    }

    @Test
    void shouldFindAllByownerIdIdWithDifferentownerIds() {
        // Given
        User ownerId1 = User.builder().name("ownerId 1").email("ownerId1@example.com").build();
        User ownerId2 = User.builder().name("ownerId 2").email("ownerId2@example.com").build();
        User savedownerId1 = entityManager.persistAndFlush(ownerId1);
        User savedownerId2 = entityManager.persistAndFlush(ownerId2);

        Item item1 = Item.builder().name("Item 1").description("Desc 1").available(true).ownerId(savedownerId1.getId()).build();
        Item item2 = Item.builder().name("Item 2").description("Desc 2").available(true).ownerId(savedownerId2.getId()).build();

        entityManager.persistAndFlush(item1);
        entityManager.persistAndFlush(item2);

        // When
        List<Item> ownerId1Items = itemRepository.findAllByOwnerId(savedownerId1.getId());

        // Then
        assertNotNull(ownerId1Items);
        assertEquals(1, ownerId1Items.size());
        assertEquals("Item 1", ownerId1Items.get(0).getName());
    }

    @Test
    void shouldFindItemsByNameIgnoreCase() {
        // Given
        User ownerId = User.builder().name("ownerId").email("ownerId@example.com").build();
        User savedownerId = entityManager.persistAndFlush(ownerId);

        Item item1 = Item.builder().name("Laptop").description("Gaming laptop").available(true).ownerId(savedownerId.getId()).build();
        Item item2 = Item.builder().name("laptop").description("Work laptop").available(true).ownerId(savedownerId.getId()).build();
        Item item3 = Item.builder().name("LAPTOP").description("Old laptop").available(true).ownerId(savedownerId.getId()).build();
        Item item4 = Item.builder().name("Mouse").description("Computer mouse").available(true).ownerId(savedownerId.getId()).build();

        entityManager.persistAndFlush(item1);
        entityManager.persistAndFlush(item2);
        entityManager.persistAndFlush(item3);
        entityManager.persistAndFlush(item4);

        // When
        List<Item> laptops = itemRepository.findItemsByNameIgnoreCase("laptop");

        // Then
        assertNotNull(laptops);
        assertEquals(3, laptops.size());
        assertTrue(laptops.stream().allMatch(item -> item.getName().toLowerCase().contains("laptop")));
    }

    @Test
    void shouldFindItemByIdAndownerIdId() {
        // Given
        User ownerId = User.builder().name("ownerId").email("ownerId@example.com").build();
        User savedownerId = entityManager.persistAndFlush(ownerId);

        Item item = Item.builder()
                .name("Specific Item")
                .description("Specific Description")
                .available(true)
                .ownerId(savedownerId.getId())
                .build();
        Item savedItem = entityManager.persistAndFlush(item);

        // When
        Optional<Item> found = itemRepository.findItemByIdAndOwnerId(savedItem.getId(), savedownerId.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("Specific Item", found.get().getName());
        assertEquals(savedownerId.getId(), found.get().getOwnerId());
    }

    @Test
    void shouldFindAllByRequestId() {
        // Given
        User requester = User.builder().name("Requester").email("requester@example.com").build();
        User ownerId = User.builder().name("ownerId").email("ownerId@example.com").build();
        User savedRequester = entityManager.persistAndFlush(requester);
        User savedownerId = entityManager.persistAndFlush(ownerId);

        ItemRequest request = ItemRequest.builder()
                .description("Need items")
                .requester(savedRequester)
                .created(LocalDateTime.now())
                .build();
        ItemRequest savedRequest = entityManager.persistAndFlush(request);

        Item item1 = Item.builder()
                .name("Item for request 1")
                .description("Description 1")
                .available(true)
                .ownerId(savedownerId.getId())
                .request(savedRequest)
                .build();

        Item item2 = Item.builder()
                .name("Item for request 2")
                .description("Description 2")
                .available(true)
                .ownerId(savedownerId.getId())
                .request(savedRequest)
                .build();

        entityManager.persistAndFlush(item1);
        entityManager.persistAndFlush(item2);

        // When
        List<Item> items = itemRepository.findAllByRequestId(savedRequest.getId());

        // Then
        assertNotNull(items);
        assertEquals(2, items.size());
        assertTrue(items.stream().allMatch(item -> item.getRequest().getId().equals(savedRequest.getId())));
        assertTrue(items.stream().anyMatch(item -> item.getName().equals("Item for request 1")));
        assertTrue(items.stream().anyMatch(item -> item.getName().equals("Item for request 2")));
    }

    @Test
    void shouldFindAllByRequestIdWithDifferentRequests() {
        // Given
        User requester = User.builder().name("Requester").email("requester@example.com").build();
        User ownerId = User.builder().name("ownerId").email("ownerId@example.com").build();
        User savedRequester = entityManager.persistAndFlush(requester);
        User savedownerId = entityManager.persistAndFlush(ownerId);

        ItemRequest request1 = ItemRequest.builder().description("Request 1").requester(savedRequester).created(LocalDateTime.now()).build();
        ItemRequest request2 = ItemRequest.builder().description("Request 2").requester(savedRequester).created(LocalDateTime.now()).build();
        ItemRequest savedRequest1 = entityManager.persistAndFlush(request1);
        ItemRequest savedRequest2 = entityManager.persistAndFlush(request2);

        Item item1 = Item.builder().name("Item 1").description("Desc 1").available(true).ownerId(savedownerId.getId()).request(savedRequest1).build();
        Item item2 = Item.builder().name("Item 2").description("Desc 2").available(true).ownerId(savedownerId.getId()).request(savedRequest2).build();

        entityManager.persistAndFlush(item1);
        entityManager.persistAndFlush(item2);

        // When
        List<Item> request1Items = itemRepository.findAllByRequestId(savedRequest1.getId());

        // Then
        assertNotNull(request1Items);
        assertEquals(1, request1Items.size());
        assertEquals("Item 1", request1Items.get(0).getName());
        assertEquals(savedRequest1.getId(), request1Items.get(0).getRequest().getId());
    }
}