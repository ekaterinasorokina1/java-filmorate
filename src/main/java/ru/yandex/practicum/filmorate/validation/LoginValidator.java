package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.NotEmptySpaces;

public class LoginValidator implements ConstraintValidator<NotEmptySpaces, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.matches("\\S+");
    }
}