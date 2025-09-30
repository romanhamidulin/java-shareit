package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {
    @Positive
    @NotNull
    private Long id;
    @Length(min = 5, max = 30)
    @NotNull
    private String name;
    @Length(min = 1, max = 500)
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    @Positive
    @NotNull
    private Long ownerId;
    private Long requestId;

}
