package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.io.IOException;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTest {
    public FilmControllerTest() throws IOException {
    }

    @Autowired
    TestRestTemplate template;

    @Test
    void get400StatusIfEmptyDuration() throws IOException, InterruptedException {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("");
        film.setReleaseDate(LocalDate.of(2024, 10, 2));
        ResponseEntity<Film> entity = template.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    }

    @Test
    void get400StatusIfNegativeDuration() throws IOException, InterruptedException {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("");
        film.setReleaseDate(LocalDate.of(2024, 10, 2));
        film.setDuration(-1);

        ResponseEntity<Film> entity = template.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    }

    @Test
    void get400StatusWhenCreateFilmWithEmptyName() throws IOException, InterruptedException {
        Film film = new Film();
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2024, 10, 2));
        film.setDuration(115);
        ResponseEntity<Film> entity = template.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());

    }

    @Test
    void get400StatusWhenCreateFilmWithEarlyReleaseDate() throws IOException, InterruptedException {
        Film film = new Film();
        film.setName("Test old Release Date");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1784, 10, 2));
        film.setDuration(115);

        ResponseEntity<Film> entity = template.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    }
}