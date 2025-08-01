package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.annotation.MinReleaseDate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.lang.reflect.Field;
import java.time.LocalDate;

public class ValidateReleaseDate {
    public static void validateFilm(Film film) {
        Field[] fields = film.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MinReleaseDate.class)) {
                validateDate(film);
            }
        }
    }

    public static void validateDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза меньше 28 декабря 1895 года");
        }
    }
}
