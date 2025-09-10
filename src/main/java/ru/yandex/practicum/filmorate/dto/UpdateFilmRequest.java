package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.MinReleaseDate;

import java.time.LocalDate;
import java.util.*;

@Data
public class UpdateFilmRequest {
    @NotNull
    private int id;
    private String name;

    @Size(max = 200)
    private String description;

    @MinReleaseDate
    private LocalDate releaseDate;

    private Integer duration;

    private Set<Integer> likes = new HashSet<>();

    private List<Map<String, Integer>> genres = new ArrayList<>();

    private Map<String, Integer> mpa;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return !(releaseDate == null);
    }

    public boolean hasDuration() {
        return duration != null;
    }
}
