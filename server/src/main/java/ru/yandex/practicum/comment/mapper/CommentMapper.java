package ru.yandex.practicum.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.entity.Comment;

@UtilityClass
public class CommentMapper {
    public CommentDto entityItemToDto(Comment comment) {
        return CommentDto.builder()
                .withId(comment.getId())
                .withAuthorName(comment.getUser().getName())
                .withText(comment.getText())
                .withCreated(comment.getCreated())
                .build();
    }
}

