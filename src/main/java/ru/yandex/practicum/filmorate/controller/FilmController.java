package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза меньше 28 декабря 1895 года");
            throw new ValidationException("дата релиза — должна быть не раньше 28 декабря 1895 года");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Длина описания превышает 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм с id = {} создан", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Отсутсвует id");
            throw new ValidationException("Id должен быть указан");
        }
        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза не должна быть меньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — должна быть не раньше 28 декабря 1895 года");
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
        throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
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
