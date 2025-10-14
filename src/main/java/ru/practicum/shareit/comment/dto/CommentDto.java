package ru.practicum.shareit.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder(setterPrefix = "with")
public class CommentDto {

    private Long id;
    @NotBlank
    @Size(max = 1000)
    private String text;
    private String authorName;
    private LocalDateTime created;
}
