package ru.yandex.practicum.filmorate.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class FilmServiceTest {
    @Autowired
    protected UserService userService;
    @Autowired
    protected FilmService filmService;
    @Autowired
    protected JdbcTemplate jdbc;

    @BeforeEach
    void beforeEach() {
        jdbc.update("DELETE FROM likes");
        jdbc.update("DELETE FROM film");
        jdbc.execute("ALTER TABLE film ALTER COLUMN film_id RESTART WITH 1");
    }

    @Test
    public void testCreateFilm() {
        FilmDto film = createNewFilm("1");
        assertThat(film)
                .hasFieldOrPropertyWithValue("name", "1test")
                .hasFieldOrPropertyWithValue("description", "1test description")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2025, 5, 4))
                .hasFieldOrPropertyWithValue("duration", 10);
    }

    @Test
    public void testGetFilmById() {
        createNewFilm("1");
        FilmDto filmDto = filmService.getFilm(1);
        assertThat(filmDto).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    public void testGetAll() {
        createNewFilm("1");
        createNewFilm("2");
        List<FilmDto> films = filmService.getAll();
        assertThat(films).asList().size().isEqualTo(2);
    }

    @Test
    public void getPopularFilms() {
        FilmDto film1 = createNewFilm("1");
        FilmDto film2 = createNewFilm("2");
        UserDto user = createUser("1");
        UserDto user2 = createUser("2");

        filmService.setLike(film2.getId(), user.getId());
        filmService.setLike(film2.getId(), user2.getId());
        filmService.setLike(film1.getId(), user.getId());

        List<FilmDto> films = filmService.getPopularFilms(4);
        assertThat(films).asList().size().isEqualTo(2);
        assertThat(films).asList().first().hasFieldOrPropertyWithValue("id", film2.getId());
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

    private UserDto createUser(String id) {
        NewUserRequest userTestRequest = new NewUserRequest();
        userTestRequest.setEmail(id + "test@mail.ru");
        userTestRequest.setName(id + "test");
        userTestRequest.setLogin(id + "testLogin");
        userTestRequest.setBirthday(LocalDate.of(2025, 5, 4));
        return userService.createUser(userTestRequest);
    }
}
