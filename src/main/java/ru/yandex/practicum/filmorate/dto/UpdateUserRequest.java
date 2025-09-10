package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    @NotNull
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }
    public boolean hasLogin() {
        return ! (login == null || login.isBlank());
    }
    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }
    public boolean hasBirthday() {
        return ! (birthday == null);
    }
}
