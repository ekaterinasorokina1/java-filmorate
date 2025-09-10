package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String FIND_FILM_GENRES_BY_ID = "SELECT g.genre_id AS genre_id, g.name AS genre_name " +
            "FROM genre AS g " +
            "JOIN film_genre AS fg ON g.genre_id = fg.genre_id " +
            "WHERE fg.film_id = ? " +
            "ORDER BY g.genre_id";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> getById(int genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public List<Genre> getFilmGenres(int filmId) {
        return jdbc.query(FIND_FILM_GENRES_BY_ID, mapper, filmId);
    }
}
