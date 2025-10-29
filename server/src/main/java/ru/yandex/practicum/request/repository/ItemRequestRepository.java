package ru.yandex.practicum.request.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.request.entity.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @EntityGraph(attributePaths = "items")
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId);

    @EntityGraph(attributePaths = "items")
    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(Long requesterId);


}
