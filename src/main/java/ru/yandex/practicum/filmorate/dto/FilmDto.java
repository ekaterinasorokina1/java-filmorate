package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.*;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private List<Genre> genres = new ArrayList<>();
    private Rating mpa;
}
