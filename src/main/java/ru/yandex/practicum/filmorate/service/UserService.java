package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void setFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        log.info("Пользователи с id = {} и {} стали друзьями", userId, friendId);
        user.getFriends().put(friendId, FriendStatus.CONFIRMED);
        friend.getFriends().put(userId, FriendStatus.CONFIRMED);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        log.info("Пользователи с id = {} и {} удалили себя из друзей", userId, friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(int userId) {
        User user = getUser(userId);
        return user.getFriends()
                .keySet()
                .stream()
                .map(this::getUser)
                .toList();
    }

    public List<User> getCommonFriend(int userId, int otherId) {
        User user = getUser(userId);
        User otherUser = getUser(otherId);
        List<User> commonFriends = new ArrayList<>();
        Set<Integer> otherUserFriends = otherUser.getFriends().keySet();

        for (Integer friendId : user.getFriends().keySet()) {
            if (otherUserFriends.contains(friendId)) {
                Optional<User> friend = userStorage.getById(friendId);
                friend.ifPresent(commonFriends::add);
            }
        }
        return commonFriends;
    }

    private User getUser(int userId) {
        Optional<User> user = userStorage.getById(userId);
        if (user.isEmpty()) {
            log.error("Отсутсвует пользователь с id = {}", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        return user.get();
    }
}
