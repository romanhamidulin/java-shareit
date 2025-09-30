package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers()
                .stream()
                .map(UserMapper::entityUserToDto)
                .toList();
    }

    @Override
    public UserDto getById(long userId) {
        User userEntity = userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID %d - не существует!".formatted(userId)));
        return UserMapper.entityUserToDto(userEntity);
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (userDto.getEmail() == null)
            throw new ValidationException("Email не должен быть пуст!");
        emailValidate(userDto);
        return UserMapper.entityUserToDto(userRepository.addUser(userDto));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        emailValidate(userDto);
        return UserMapper.entityUserToDto(userRepository.updateUser(userDto, userId));
    }

    @Override
    public void deleteById(long userId) {
        userRepository.deleteById(userId);
    }

    private void emailValidate(UserDto userDto) {
        if (userDto.getEmail() != null) {
            boolean emailAlreadyUse = userRepository.getAllUsers()
                    .stream()
                    .map(User::getEmail)
                    .anyMatch(userDto.getEmail()::equals);
            if (emailAlreadyUse)
                throw new ExistException("Пользователь с таким email %s - уже существует!".formatted(userDto.getEmail()));
        }
    }
}
