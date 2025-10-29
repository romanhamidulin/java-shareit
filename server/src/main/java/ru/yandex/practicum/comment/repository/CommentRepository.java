package ru.yandex.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
