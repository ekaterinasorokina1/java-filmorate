package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с id = {} создан", film.getId());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Отсутсвует id");
            throw new ValidationException("Id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            Film film = films.get(newFilm.getId());
            film.setName(newFilm.getName());
            film.setReleaseDate(newFilm.getReleaseDate());
            film.setDuration(newFilm.getDuration());

            if (newFilm.getDescription() != null && newFilm.getDescription().length() <= 200) {
                film.setDescription(newFilm.getDescription());
            }
            log.info("Фильм с id = {} обновлен", newFilm.getId());
            return film;
        }
        log.error("Фильм с id = {}", newFilm.getId() + " не найден");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Optional<Film> getById(int filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
