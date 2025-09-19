package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAll();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> getById(int filmId);

    void setLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> getPopular(int count);

    List<Film> getCommonFilms(int userId, int filmId);
}
