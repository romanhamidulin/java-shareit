package ru.yandex.practicum.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.item.dto.CommentDto;
import ru.yandex.practicum.item.dto.ItemDto;

import java.util.Collections;

@Validated
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive Long id,
                                              @RequestHeader(REQUEST_HEADER) @Positive Long userId) {
        log.info("Gateway sent request [getItemById]: id={}", id);
        return itemClient.getItemById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader(REQUEST_HEADER) @Positive Long userId) {
        return itemClient.getAllItemsByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> postItem(@RequestHeader(REQUEST_HEADER) @Positive Long userId,
                                           @Valid @RequestBody ItemDto dto) {
        return itemClient.createItem(userId, dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchItem(@RequestHeader(REQUEST_HEADER) @Positive Long userId,
                                            @PathVariable Long id, @RequestBody @Valid ItemDto dto) {
        return itemClient.updateItem(id, userId, dto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestParam String text) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.searchByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(REQUEST_HEADER) @Positive Long userId,
                                              @PathVariable @Positive Long itemId, @RequestBody @Valid CommentDto dto) {
        return itemClient.createComment(userId, itemId, dto);
    }
}
