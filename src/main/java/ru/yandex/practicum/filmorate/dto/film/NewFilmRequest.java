package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.MinReleaseDate;

import java.time.LocalDate;
import java.util.*;

@Data
public class NewFilmRequest {
    @NotBlank
    @NotNull
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    @MinReleaseDate
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Set<Integer> likes = new HashSet<>();

    private List<Map<String, Integer>> genres = new ArrayList<>();

    @NotNull
    private Map<String, Integer> mpa;
}
