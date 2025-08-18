package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getAll();

    User create(User user);

    User update(User user);

    Optional<User> getById(int userId);
}
