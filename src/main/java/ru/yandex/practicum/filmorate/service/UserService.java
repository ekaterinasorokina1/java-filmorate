package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperationType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;
    private final GenreStorage genreStorage;

    public UserDto createUser(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);

        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        user = userStorage.create(user);
        log.info("Добавление пользователя: {}", user);

        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getUsers() {
        log.info("Получение списка пользователей");

        return userStorage.getAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(int userId) {
        log.info("Получение пользователя с id = {}", userId);

        return userStorage.getById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
    }

    public UserDto updateUser(UpdateUserRequest request) {
        User updatedUser = userStorage.getById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (updatedUser.getName().isEmpty()) {
            updatedUser.setName(updatedUser.getLogin());
        }

        updatedUser = userStorage.update(updatedUser);
        log.info("Обновление пользователя с id = {}", request.getId());

        return UserMapper.mapToUserDto(updatedUser);
    }

    public void setFriend(int userId, int friendId) {
        validateUser(friendId);

        User user = userStorage.getById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        if (!user.getFriends().contains(friendId)) {
            userStorage.addFriend(userId, friendId);

            feedStorage.add(userId, FeedEventType.FRIEND, FeedOperationType.ADD, friendId);
        }
    }

    public void deleteFriend(int userId, int friendId) {
        validateUser(userId);
        validateUser(friendId);

        userStorage.deleteFriend(userId, friendId);

        feedStorage.add(userId, FeedEventType.FRIEND, FeedOperationType.REMOVE, friendId);
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

    public List<Feed> getFeed(int userId) {
        validateUser(userId);
        return feedStorage.getAll(userId);
    }

    private void validateUser(int userId) {
        userStorage.getById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    public List<FilmDto> getRecomendations(int userId) {
        validateUser(userId);
        Integer mostCommonFilmsUserId = userStorage.getAll().stream()
                .filter(user -> user.getId() != userId)
                .map(user -> Map.entry(user.getId(), filmStorage.getCommonFilms(userId, user.getId()).size()))
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);

        return filmStorage.getLikedFilms(mostCommonFilmsUserId).stream()
                .filter(film -> !filmStorage.getLikedFilms(userId).contains(film))
                .peek(film -> film.setGenres(genreStorage.getFilmGenres(film.getId())))
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public void deleteById(int id) {
        validateUser(id);
        userStorage.deleteById(id);
        log.info("Пользователь {} удален", id);
    }
}