package ru.yandex.practicum.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.item.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> findItemsByNameIgnoreCase(String name);

    Optional<Item> findItemByIdAndOwnerId(Long id, Long ownerId);

    List<Item> findAllByRequestId(Long requestId);
}

