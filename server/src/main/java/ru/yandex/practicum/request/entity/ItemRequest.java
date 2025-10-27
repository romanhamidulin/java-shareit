package ru.yandex.practicum.request.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.item.entity.Item;
import ru.yandex.practicum.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@Entity
@Table(name = "item_requests")
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_id")
    private User requester;
    private Long requestId;
    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY)
    private List<Item> items;
    private LocalDateTime created;
}
