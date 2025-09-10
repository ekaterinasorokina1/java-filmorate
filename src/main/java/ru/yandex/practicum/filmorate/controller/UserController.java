package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStorage userStorage;

    @GetMapping
    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userStorage.update(newUser);
    }

    @GetMapping("/{id}")
    public Optional<User> getById(@PathVariable int id) {
        return userStorage.getById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void setFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.setFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriend(id, otherId);
    }

}
