package ru.yandex.practicum.filmorate.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class GenreServiceTest {
    @Autowired
    GenreStorage genreStorage;
    @Autowired
    protected FilmService filmService;
    @Autowired
    protected JdbcTemplate jdbc;

    @BeforeEach
    void beforeEach() {
        jdbc.update("DELETE FROM film_genre");
        jdbc.update("DELETE FROM film");
        jdbc.execute("ALTER TABLE film ALTER COLUMN film_id RESTART WITH 1");
    }

    @Test
    public void testGetFilmGenre() {
        FilmDto film2 = createNewFilm("1");
        List<Genre> genres = genreStorage.getFilmGenres(film2.getId());
        String genre = genres.getFirst().getName().toString();
        assertThat(genres).asList().size().isEqualTo(1);
        assertThat(genre).isEqualTo("Комедия");
    }

    @Test
    public void testGetAllGenre() {
        List<Genre> ratingList = genreStorage.getAll();

        assertThat(ratingList).asList().size().isEqualTo(6);
    }

    private FilmDto createNewFilm(String id) {
        NewFilmRequest newFilm = new NewFilmRequest();
        newFilm.setDescription(id + "test description");
        newFilm.setName(id + "test");
        newFilm.setReleaseDate(LocalDate.of(2025, 5, 4));
        newFilm.setDuration(10);
        Map<String, Integer> mpa = new HashMap<>();
        mpa.put("id", 1);
        newFilm.setMpa(mpa);
        Map<String, Integer> genre = new HashMap<>();
        genre.put("id", 1);
        newFilm.setGenres(List.of(genre));
        return filmService.create(newFilm);
    }
}
