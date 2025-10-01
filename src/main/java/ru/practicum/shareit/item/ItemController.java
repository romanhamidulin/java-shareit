package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String REQUEST_HEADER="X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllUsersItems(@RequestHeader(REQUEST_HEADER) Long userId) {
        return itemService.getAllUsersItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId, @RequestHeader(REQUEST_HEADER) Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByText(@RequestParam String text) {
        return itemService.getItemsByText(text);
    }

    @PostMapping
    public ItemDto createNewItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(REQUEST_HEADER) Long userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @Positive @PathVariable Long itemId,
                              @Positive @RequestHeader(REQUEST_HEADER) Long userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteById(@PathVariable Long itemId) {
        itemService.deleteById(itemId);
    }
}
