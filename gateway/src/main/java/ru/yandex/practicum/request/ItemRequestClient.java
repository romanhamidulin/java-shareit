package ru.yandex.practicum.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.client.BaseClient;
import ru.yandex.practicum.request.dto.ItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String ENDPOINT = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String host, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(host + ENDPOINT))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> getItemRequestById(Long requestId) {
        return get("/" + requestId);
    }

    public ResponseEntity<Object> getItemRequests(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllItemRequests(Long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> createItemRequest(ItemRequestDto item, Long userId) {
        return post("", userId, item);
    }
}
