package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import java.util.List;

@Slf4j
@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getAll() {
        log.info("Получение списка режиссёров");

        return directorStorage.getAll();
    }

    public Director getById(int id) {
        log.info("Получение режиссёра с id = {}", id);

        return directorStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id = " + id + " не найден"));
    }

    public Director create(Director director) {
        log.info("Добавление режиссёра: {}", director);

        return directorStorage.create(director);
    }

    public Director update(Director director) {
        getById(director.getId());
        log.info("Обновление режиссёра с id = {}", director.getId());

        return directorStorage.update(director);
    }

    public void deleteById(int id) {
        getById(id);
        directorStorage.deleteById(id);
        log.info("Режиссёр {} удален", id);
    }

    public List<Director> getFilmDirectors(int filmId) {
        log.info("Получение списка режиссёров для фильма с id = {}", filmId);

        return directorStorage.getFilmDirectors(filmId);
    }
}