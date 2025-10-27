package ru.yandex.practicum.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.item.dto.ItemDto;
import ru.yandex.practicum.item.dto.ItemDtoWithDate;
import ru.yandex.practicum.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllUsersItems(@RequestHeader(REQUEST_HEADER) Long userId) {
        return itemService.getAllUsersItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithDate getItemById(@PathVariable Long itemId, @RequestHeader(REQUEST_HEADER) Long userId) {
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

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(@Valid @RequestBody CommentDto commentDto,
                                    @Positive @PathVariable Long itemId,
                                    @RequestHeader(REQUEST_HEADER) Long userId) {
        return itemService.addComment(commentDto, itemId, userId);
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
