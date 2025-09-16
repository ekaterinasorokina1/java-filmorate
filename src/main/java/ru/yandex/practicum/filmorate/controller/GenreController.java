package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreStorage genreStorage;

    @GetMapping
    public List<Genre> getAll() {
        return genreStorage.getAll();
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable int id) {
        return genreStorage.getById(id).orElseThrow(() -> new NotFoundException("Жанр не найден"));
    }

}
