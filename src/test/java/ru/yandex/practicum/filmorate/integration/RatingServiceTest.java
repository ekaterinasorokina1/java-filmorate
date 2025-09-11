package ru.yandex.practicum.filmorate.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class RatingServiceTest {
    @Autowired
    protected RatingStorage ratingStorage;
    @Autowired
    protected FilmService filmService;
    @Autowired
    protected JdbcTemplate jdbc;

    @BeforeEach
    void beforeEach() {
        jdbc.update("DELETE FROM film");
        jdbc.execute("ALTER TABLE film ALTER COLUMN film_id RESTART WITH 1");
    }

    @Test
    public void testGetRating() {
        FilmDto film2 = createNewFilm("1");
        Rating rating = ratingStorage.findById(film2.getMpa().getId()).orElseThrow(() -> new NotFoundException("Рейтинга нет"));
        assertThat(rating).hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    public void testGetAllRating() {
        List<Rating> ratingList = ratingStorage.getAll();

        assertThat(ratingList).asList().size().isEqualTo(5);
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
        return filmService.create(newFilm);
    }
}
