package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.entity.Comment;

public class CommentMapper {
    public static CommentDto entityItemToDto(Comment comment) {
        return CommentDto.builder()
                .withId(comment.getId())
                .withAuthorName(comment.getUser().getName())
                .withText(comment.getText())
                .withCreated(comment.getCreated())
                .build();
    }
}
