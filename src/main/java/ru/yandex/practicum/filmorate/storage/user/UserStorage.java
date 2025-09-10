package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getAll();

    User create(User user);

    User update(User user);

    Optional<User> getById(int userId);

    void addFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    void deleteFriend(int userId, int friendId);

    List<User> getCommonFriend(int userId, int otherId);
}
