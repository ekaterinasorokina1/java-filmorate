package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;


@Data
public class Film {
    private Integer id;

    @NotBlank
    @NotNull
    private String name;

    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Positive
    public int duration;
}
