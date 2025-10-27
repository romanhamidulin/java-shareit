package ru.yandex.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.request.dto.ItemRequestDto;
import ru.yandex.practicum.request.service.ItemRequestService;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemRequestDto> getCurrentRequests(
            @RequestHeader(REQUEST_HEADER) Long userId
    ) {
        return itemRequestService.getCurrentRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader(REQUEST_HEADER) Long userId
    ) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId,
                                                 @RequestHeader(REQUEST_HEADER) Long userId) {
        return itemRequestService.getRequestById(requestId, userId);
    }

    @PostMapping
    public ItemRequestDto createRequest(
            @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(REQUEST_HEADER) Long userId
    ) {
        return itemRequestService.createRequest(itemRequestDto, userId);
    }
}
