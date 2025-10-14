package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final HashMap<Long, Item> items = new HashMap<>();
    private long id = 0;

    @Override
    public List<Item> getAllUsersItems(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .toList();
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return Optional.of(items.get(itemId));
    }

    @Override
    public List<Item> getItemsByText(String text) {
        String searchingText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(searchingText)
                        || item.getDescription().toLowerCase().contains(searchingText))
                .filter(Item::getAvailable)
                .toList();
    }

    @Override
    public Item addItem(ItemDto itemDto, long userId) {
        Item itemToPut = Item.builder()
                .id(getNextId())
                .ownerId(userId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();

        items.put(itemToPut.getId(), itemToPut);
        return itemToPut;
    }

    @Override
    public Item updateItem(ItemDto itemDto, long itemId, long userId) {
        Item itemToChange = items.get(itemId);
        if (itemDto.getName() != null) {
            itemToChange.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemToChange.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemToChange.setAvailable(itemDto.getAvailable());
        }
        return Objects.requireNonNull(items.put(itemId, itemToChange));
    }

    @Override
    public void deleteById(long itemId) {
        items.remove(itemId);
    }

    private long getNextId() {
        if (items.isEmpty()) {
            return 0;
        } else {
            return ++id;
        }
    }
}
