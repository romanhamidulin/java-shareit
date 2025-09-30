package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private static final HashMap<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> getById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User addUser(UserDto userDto) {
        User userToPut = User.builder()
                .id(getNextId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
        users.put(userToPut.getId(), userToPut);
        log.info("Cоздан пользователь с ID = {} | {}", userToPut.getId(), userToPut);
        return userToPut;
    }

    @Override
    public User updateUser(UserDto userDto, Long userId) {
        log.info("Пришёл на обновление {} с ID {}", userDto, userId);
        User userToUpdate = users.get(userId);
        userToUpdate.setName(userDto.getName());
        userToUpdate.setEmail(userDto.getEmail());
        users.put(userId, userToUpdate);
        log.info("Обновлён пользователь с ID = {} | {}", userToUpdate.getId(), userToUpdate);
        return userToUpdate;
    }

    @Override
    public void deleteById(Long userId) {
        if (!users.containsKey(userId))
            throw new NotFoundException("Пользователя с ID %d - не существует!".formatted(userId));
        users.remove(userId);
        log.info("Удалён пользователь с ID = {}", userId);
    }

    private long getNextId() {
        if (users.isEmpty()) {
            return 0;
        } else {
            return ++id;
        }
    }
}
