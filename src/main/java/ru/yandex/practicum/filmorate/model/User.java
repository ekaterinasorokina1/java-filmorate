package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.NotEmptySpaces;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    private Integer id;

    @NotNull
    @Email
    private String email;

    @NotBlank
    @NotNull
    @NotEmptySpaces
    private String login;

    private String name;

    @Past
    private LocalDate birthday;

    private Map<Integer, FriendStatus> friends = new HashMap<>();
}
