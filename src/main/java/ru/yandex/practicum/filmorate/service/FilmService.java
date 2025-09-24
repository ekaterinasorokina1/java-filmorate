package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperationType;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    private final DirectorService directorService;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RatingStorage ratingStorage;
    private final GenreStorage genreStorage;
    private final FeedStorage feedStorage;
    private final DirectorStorage directorStorage;

    public List<FilmDto> getPopularFilms(Integer count, Integer genreId, Integer year) {
        List<Film> films = filmStorage.getPopular(count, genreId, year);
        return mapFilmListToDto(films);
    }

    public FilmDto getFilm(int filmId) {
        Optional<Film> film = filmStorage.getById(filmId);
        if (film.isEmpty()) {
            log.error("Отсутствует фильм с id = {}", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        Film filmExist = film.get();
        filmExist.setGenres(genreStorage.getFilmGenres(filmId));
        filmExist.setDirectors(directorService.getFilmDirectors(filmId));

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

        if (!request.getDirectors().isEmpty()) {
            validateDirectors(request.getDirectors().stream().map(director -> director.get("id")).collect(Collectors.toList()));
        }

        List<Genre> genres = new ArrayList<>();
        new HashSet<>(request.getGenres()).forEach(genre -> {
            genres.add(genreStorage.getById(genre.get("id")).orElseThrow(() -> new NotFoundException("Такого жанра нет")));
        });

        List<Director> directors = new ArrayList<>();
        new HashSet<>(request.getDirectors()).forEach(director -> {
            directors.add(directorService.getById(director.get("id")));
        });

        Film film = FilmMapper.mapToFilm(request);

        genres.sort(Comparator.comparingInt(Genre::getId));
        film.setGenres(genres);
        film.setDirectors(directors);
        film.setRating(ratingStorage.findById(request.getMpa().get("id")).orElseThrow(() -> new NotFoundException("Такого рейтинга нет")));

        film = filmStorage.create(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {
        Film updatedFilm = filmStorage.getById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        List<Genre> genres = new ArrayList<>();

        filmStorage.deleteFilmGenres(request.getId());

        Set<Integer> genreUniqueIds = new HashSet<>();

        request.getGenres().forEach(genre -> {
            if (!genreUniqueIds.contains(genre.get("id"))) {
                genres.add(genreStorage.getById(genre.get("id")).orElseThrow(() -> new NotFoundException("Такого жанра нет")));
                genreUniqueIds.add(genre.get("id"));
            }
        });
        updatedFilm.setGenres(genres);

        updatedFilm.setRating(ratingStorage.findById(request.getMpa().get("id")).orElseThrow(() -> new NotFoundException("Такого рейтинга нет")));

        filmStorage.deleteDirectors(request.getId());

        if (!request.getDirectors().isEmpty()) {
            validateDirectors(request.getDirectors().stream().map(director -> director.get("id")).collect(Collectors.toList()));
        }

        List<Director> directors = new ArrayList<>();
        new HashSet<>(request.getDirectors()).forEach(director -> {
            directors.add(directorService.getById(director.get("id")));
        });
        updatedFilm.setDirectors(directors);

        updatedFilm = filmStorage.update(updatedFilm);
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public void setLike(int filmId, int userId) {
        validateFilm(filmId);
        validateUser(userId);

        filmStorage.setLike(filmId, userId);

        log.info("Пользователь с id = {} добавил лайк фильму с id = {}", userId, filmId);

        feedStorage.add(userId, FeedEventType.LIKE, FeedOperationType.ADD, filmId);
    }

    public void deleteLike(int filmId, int userId) {
        validateFilm(filmId);
        validateUser(userId);
        filmStorage.deleteLike(filmId, userId);
        log.info("Пользователь с id = {} удалил лайк фильму с id = {}", userId, filmId);

        feedStorage.add(userId, FeedEventType.LIKE, FeedOperationType.REMOVE, filmId);
    }

    public List<FilmDto> getCommonFilms(int userId, int friendId) {
        validateUser(userId);
        validateUser(friendId);
        //Могу добавить валидацию что пользователи есть в друзьях друг у друга
        return mapFilmListToDto(filmStorage.getCommonFilms(userId, friendId));
    }

    public List<FilmDto> getDirectorFilms(int directorId, String sortBy) {
        validateDirectors(Collections.singletonList(directorId));

        if (!"year".equals(sortBy) && !"likes".equals(sortBy)) {
            throw new ValidationException("Сортировка фильмов может быть по количеству лайков или году выпуска");
        }

        log.info("Получение списка фильмов режиссёра с id = {}, сортировка по {}", directorId, sortBy);

        return "year".equals(sortBy)
                ? mapFilmListToDto(filmStorage.getDirectorFilmsByYear(directorId))
                : mapFilmListToDto(filmStorage.getDirectorFilmsByLikes(directorId));
    }

    public void deleteById(int id) {
        validateFilm(id);
        filmStorage.deleteById(id);
        filmStorage.deleteFilmGenres(id);
        filmStorage.deleteDirectors(id);
        log.info("Фильм {} удален", id);
    }

    private List<FilmDto> mapFilmListToDto(List<Film> films) {
        films.forEach(film -> film.setGenres(genreStorage.getFilmGenres(film.getId())));
        films.forEach(film -> film.setDirectors(directorService.getFilmDirectors(film.getId())));

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








    private void validateDirectors(List<Integer> ids) {
        ids.forEach(directorId -> directorStorage.getById(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссер с id = " + directorId + " не найден")));
    }


    public List<FilmDto> searchFilms(String query, String by) {

        List<String> fields = Arrays.stream(by.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();

        List<Film> filtered = filmStorage.searchFilms(query, fields);

        return filtered.stream()
                .peek(film -> {
                    film.setDirectors(directorService.getFilmDirectors(film.getId()));
                    film.setGenres(genreStorage.getFilmGenres(film.getId()));
                })
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}
