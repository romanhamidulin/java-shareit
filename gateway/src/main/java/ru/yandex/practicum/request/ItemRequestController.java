package ru.yandex.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.request.dto.ItemRequestDto;

@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemClient;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getCurrentRequests(
            @RequestHeader(REQUEST_HEADER) Long userId
    ) {
        return itemClient.getItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(REQUEST_HEADER) Long userId
    ) {
        return itemClient.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @PathVariable Long requestId,
            @RequestHeader(REQUEST_HEADER) Long userId
    ) {
        return itemClient.getItemRequestById(requestId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(REQUEST_HEADER) Long userId
    ) {
        return itemClient.createItemRequest(itemRequestDto, userId);
    }
}
