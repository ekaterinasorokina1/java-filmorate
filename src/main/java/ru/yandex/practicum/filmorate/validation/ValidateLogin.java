package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.annotation.NotEmptySpaces;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.lang.reflect.Field;

public class ValidateLogin {
    public static void validateUser(User user) {
        Field[] fields = user.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(NotEmptySpaces.class)) {
                validateLogin(user);
            }
        }
    }

    public static void validateLogin(User user) {
        if (!user.getLogin().matches("\\S+")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
    }
}
