package ru.yandex.practicum.user.service;

import ru.yandex.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getById(long userId);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long userId);

    void deleteById(long userId);
}
