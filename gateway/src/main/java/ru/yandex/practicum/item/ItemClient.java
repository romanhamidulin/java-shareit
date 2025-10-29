package ru.yandex.practicum.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.client.BaseClient;
import ru.yandex.practicum.item.dto.CommentDto;
import ru.yandex.practicum.item.dto.ItemDto;

import java.util.Map;

@Slf4j
@Service
public class ItemClient extends BaseClient {

    private static final String ENDPOINT = "/items";

    public ItemClient(@Value("${shareit-server.url}") String host, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(host + ENDPOINT)).build());
    }

    public ResponseEntity<Object> getItemById(long id, long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getAllItemsByUserId(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> createItem(long userId, ItemDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> updateItem(long itemId, long userId, ItemDto dto) {
        return patch("/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> searchByText(String text) {
        Map<String, Object> params = Map.of(
                "text", text
        );
        return get("/search?text={text}", params);
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CommentDto dto) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return post("/{itemId}/comment", userId, parameters, dto);
    }
}

