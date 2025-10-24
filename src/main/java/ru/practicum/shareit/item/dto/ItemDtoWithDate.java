package ru.practicum.shareit.item.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class ItemDtoWithDate {
    private Long id;
    @Length(min = 5, max = 30)
    private String name;
    @Length(min = 1, max = 500)
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
}
