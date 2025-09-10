package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void setLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        User user = getUser(userId);

        film.getLikes().add(user.getId());
        log.info("Пользователь с id = {} добавил лайк фильму с id = {}", userId, filmId);
    }

    public void deleteLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        User user = getUser(userId);

        film.getLikes().remove(user.getId());
        log.info("Пользователь с id = {} удалил лайк фильму с id = {}", userId, filmId);
    }

    public List<Film> getPopularFilms(Integer count) {
        List<Film> allFilms = new ArrayList<>(filmStorage.getAll());
        allFilms.sort(Collections.reverseOrder(Comparator.comparingInt(f -> f.getLikes().size())));

        return allFilms.stream().limit(count).toList();
    }

    private Film getFilm(int filmId) {
        Optional<Film> film = filmStorage.getById(filmId);
        if (film.isEmpty()) {
            log.error("Отсутсвует фильм с id = {}", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        return film.get();
    }

    private User getUser(int userId) {
        Optional<User> user = userStorage.getById(userId);
        if (user.isEmpty()) {
            log.error("Отсутсвует пользователь с id = {}", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        return user.get();
    }
}
