package ru.practicum.shareit.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.entity.Comment;

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
