package ru.yandex.practicum.item.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.comment.dto.CommentDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    @Length(min = 5, max = 30)
    private String name;
    @Length(min = 1, max = 500)
    private String description;
    private Boolean available;
    private Long requestId;
    private List<CommentDto> comments;
}
