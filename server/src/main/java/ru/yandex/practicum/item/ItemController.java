package ru.yandex.practicum.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    public List<ItemDto> getAllUsersItems(@RequestHeader(REQUEST_HEADER) @Positive Long userId) {
        return itemService.getAllUsersItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithDate getItemById(@PathVariable @Positive Long itemId,
                                       @RequestHeader(REQUEST_HEADER) @Positive Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchingItemsByText(@RequestParam @Size(max = 300) String text) {
        return itemService.getItemsByText(text);
    }

    @PostMapping
    public ItemDto createNewItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader(REQUEST_HEADER) @Positive Long userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createNewComment(@RequestBody @Valid CommentDto commentDto,
                                       @PathVariable @Positive Long itemId,
                                       @RequestHeader(REQUEST_HEADER) @Positive Long userId) {
        return itemService.addComment(commentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody @Valid ItemDto itemDto, @PathVariable @Positive Long itemId,
                              @RequestHeader(REQUEST_HEADER) @Positive Long userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable @Positive Long itemId) {
        itemService.deleteById(itemId);
    }
}
