package ru.yandex.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.ExistException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.entity.User;
import ru.yandex.practicum.user.mapper.UserMapper;
import ru.yandex.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::entityUserToDto)
                .toList();
    }

    @Override
    public UserDto getById(long userId) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID %d - не существует!".formatted(userId)));
        return UserMapper.entityUserToDto(userEntity);
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (userDto.getEmail() == null)
            throw new ValidationException("Email не должен быть пуст!");
        emailValidate(userDto);
        return UserMapper.entityUserToDto(userRepository.save(UserMapper.dtoToEntityItem(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким ID %s - не существует!".formatted(userId)));
        emailValidate(userDto);
        String userDtoEmail = userDto.getEmail();
        if (userDtoEmail != null)
            user.setEmail(userDto.getEmail());
        if (userDto.getName() != null) user.setName(userDto.getName());
        return UserMapper.entityUserToDto(userRepository.save(user));
    }

    @Override
    public void deleteById(long userId) {
        userRepository.deleteById(userId);
    }

    private void emailValidate(UserDto userDto) {
        if (userDto.getEmail() != null) {
            boolean emailAlreadyUse = userRepository.existsUserByEmail(userDto.getEmail());
            if (emailAlreadyUse)
                throw new ExistException("Пользователь с таким email %s - уже существует!".formatted(userDto.getEmail()));
        }
    }
}
