package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RatingStorage ratingStorage;
    private final GenreStorage genreStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage, RatingStorage ratingStorage, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.ratingStorage = ratingStorage;
        this.genreStorage = genreStorage;
    }

    public List<FilmDto> getPopularFilms(Integer count) {
        List<Film> films = filmStorage.getPopular(count);
        return mapFilmListToDto(films);
    }

    public FilmDto getFilm(int filmId) {
        Optional<Film> film = filmStorage.getById(filmId);
        if (film.isEmpty()) {
            log.error("Отсутсвует фильм с id = {}", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        Film filmExist = film.get();
        filmExist.setGenres(genreStorage.getFilmGenres(filmId));
        return FilmMapper.mapToFilmDto(filmExist);
    }

    public List<FilmDto> getAll() {
        List<Film> films = filmStorage.getAll();
        return mapFilmListToDto(films);
    }

    public FilmDto create(NewFilmRequest request) {
        if (!request.getMpa().isEmpty()) {
            validateRating(request.getMpa().get("id"));
        }
        if (!request.getGenres().isEmpty()) {
            validateGenres(request.getGenres().stream().map(genre -> genre.get("id")).collect(Collectors.toList()));
        }

        List<Genre> genres = new ArrayList<>();
        new HashSet<>(request.getGenres()).forEach(genre -> {
            genres.add(genreStorage.getById(genre.get("id")).orElseThrow(() -> new NotFoundException("Такого жанра нет")));
        });

        Film film = FilmMapper.mapToFilm(request);
        film.setGenres(genres);
        film.setRating(ratingStorage.findById(request.getMpa().get("id")).orElseThrow(() -> new NotFoundException("Такого рейтинга нет")));

        film = filmStorage.create(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {
        Film updatedFilm = filmStorage.getById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        List<Genre> genres = new ArrayList<>();
        request.getGenres().forEach(genre -> {
            genres.add(genreStorage.getById(genre.get("id")).orElseThrow(() -> new NotFoundException("Такого жанра нет")));
        });
        updatedFilm.setGenres(genres);

        updatedFilm = filmStorage.update(updatedFilm);
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public void setLike(int filmId, int userId) {
        validateFilm(filmId);
        validateUser(userId);

        filmStorage.setLike(filmId, userId);

        log.info("Пользователь с id = {} добавил лайк фильму с id = {}", userId, filmId);
    }

    public void deleteLike(int filmId, int userId) {
        validateFilm(filmId);
        validateUser(userId);
        filmStorage.deleteLike(filmId, userId);
        log.info("Пользователь с id = {} удалил лайк фильму с id = {}", userId, filmId);
    }

    private List<FilmDto> mapFilmListToDto(List<Film> films) {
        films.forEach(film -> film.setGenres(genreStorage.getFilmGenres(film.getId())));

        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    private void validateRating(int ratingId) {
        ratingStorage.findById(ratingId).orElseThrow(() -> new NotFoundException("Такого рейтинга нет"));
    }

    private void validateGenres(List<Integer> genreIds) {
        genreIds.forEach(genreId -> genreStorage.getById(genreId).orElseThrow(() -> new NotFoundException("Такого жанра нет")));
    }

    private void validateFilm(int filmId) {
        filmStorage.getById(filmId).orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
    }

    private void validateUser(int userId) {
        userStorage.getById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
