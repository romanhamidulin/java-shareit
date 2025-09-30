package ru.practicum.shareit.request.dto;

import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private String description;
    private Long requestor;
}
