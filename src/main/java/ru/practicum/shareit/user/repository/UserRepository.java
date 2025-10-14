package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAllUsers();

    Optional<User> getById(long userId);

    User addUser(UserDto userDto);

    User updateUser(UserDto userDto, Long userId);

    void deleteById(Long userId);
}