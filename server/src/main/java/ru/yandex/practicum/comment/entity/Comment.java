package ru.yandex.practicum.comment.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.item.entity.Item;
import ru.yandex.practicum.user.entity.User;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "name")
    private User user;

    private LocalDateTime created;

    public Comment() {
    }
}

