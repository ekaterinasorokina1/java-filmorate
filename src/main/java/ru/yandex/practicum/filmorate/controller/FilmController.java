package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
//    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping
    public List<FilmDto> getAllFilms() {
        return filmService.getAll();
    }

    @PostMapping
    public FilmDto create(@RequestBody @Valid NewFilmRequest filmRequest) {
        return filmService.create(filmRequest);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest newFilm) {
        return filmService.update(newFilm);
    }

    @GetMapping("/{id}")
    public FilmDto getById(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable int id, @PathVariable int userId) {
        filmService.setLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}
