package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT f.*, r.name rating_name, FROM film f " +
            "LEFT JOIN rating r ON f.rating_id = r.rating_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, r.name rating_name " +
            "FROM film f " +
            "LEFT JOIN rating r ON f.rating_id = r.rating_id " +
            "WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film(name, description, releaseDate, duration, rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE film SET name = ?, description = ?, releaseDate = ?, duration = ? WHERE film_id = ?";
    private static final String INSERT_INTO_FILM_GENRES = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String UPDATE_QUERY_ADD_LIKE = "INSERT INTO likes(film_id, user_id)" + "VALUES(?, ?)";
    private static final String DELETE_QUERY_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_POPULAR = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, r.name rating_name  " +
            "FROM film f " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "LEFT JOIN rating r ON f.rating_id = r.rating_id " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";
    private static final String GET_COMMON_QUERY = "SELECT f.*, r.name rating_name" +
            " FROM film f" +
            " JOIN likes l1 ON f.film_id = l1.film_id" +
            " JOIN likes l2 ON f.film_id = l2.film_id" +
            " LEFT JOIN rating r ON f.rating_id = r.rating_id" +
            " WHERE l1.user_id = ?" +
            "   AND l2.user_id = ?";

    private final JdbcTemplate jdbc;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
        this.jdbc = jdbc;
    }

    public List<Film> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Film create(Film film) {
        int id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRating().getId()
        );
        film.setId(id);
        film.getGenres().forEach(genre -> setFilmGenres(id, genre.getId()));

        return film;
    }

    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );
        film.getGenres().forEach(genre -> setFilmGenres(film.getId(), genre.getId()));
        return film;
    }

    public Optional<Film> getById(int filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    public void setLike(int filmId, int userId) {
        update(UPDATE_QUERY_ADD_LIKE, filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        update(DELETE_QUERY_LIKE, filmId, userId);
    }

    public List<Film> getPopular(int count) {
        return findMany(GET_POPULAR, count);
    }

    private void setFilmGenres(Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_INTO_FILM_GENRES, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return findMany(GET_COMMON_QUERY, userId, friendId);
    }
}
