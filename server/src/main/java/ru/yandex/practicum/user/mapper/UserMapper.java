package ru.yandex.practicum.user.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.entity.User;

@UtilityClass
public class UserMapper {
    public UserDto entityUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User dtoToEntityItem(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
