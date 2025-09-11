package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {
    private final RatingStorage ratingStorage;

    @GetMapping
    public List<Rating> getAll() {
        return ratingStorage.getAll();
    }

    @GetMapping("/{id}")
    public Rating getRating(@PathVariable int id) {
        return ratingStorage.findById(id).orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
    }
}
