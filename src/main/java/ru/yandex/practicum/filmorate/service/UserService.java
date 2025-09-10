package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);

        user = userStorage.create(user);

        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getUsers() {
        return userStorage.getAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(int userId) {
        return userStorage.getById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
    }

    public UserDto updateUser(UpdateUserRequest request) {
        User updatedUser = userStorage.getById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updatedUser = userStorage.update(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void setFriend(int userId, int friendId) {
        validateUser(friendId);

        Optional<User> user = userStorage.getById(userId);
        if (user.isEmpty()) {
            log.error("Отсутсвует Пользователь с id = {}", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (!user.get().getFriends().containsKey(friendId)) {
            userStorage.addFriend(userId, friendId);
        }
    }

    public void deleteFriend(int userId, int friendId) {
        validateUser(userId);
        validateUser(friendId);

        userStorage.deleteFriend(userId, friendId);
    }

    public List<UserDto> getFriends(int userId) {
        validateUser(userId);

        return userStorage.getFriends(userId).stream().map(UserMapper::mapToUserDto).toList();
    }

    public List<UserDto> getCommonFriend(int userId, int otherId) {
        validateUser(otherId);
        validateUser(userId);

        return userStorage.getCommonFriend(userId, otherId).stream().map(UserMapper::mapToUserDto).toList();
    }

    private void validateUser(int userId) {
        Optional<User> user = userStorage.getById(userId);
        if (user.isEmpty()) {
            log.error("Отсутсвует Пользователь с id = {}", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}
