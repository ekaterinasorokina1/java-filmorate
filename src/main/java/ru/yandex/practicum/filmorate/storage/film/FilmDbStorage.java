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
    private static final String INSERT_INTO_FILM_DIRECTORS = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";
    private static final String DELETE_FILM_DIRECTORS = "DELETE FROM film_director WHERE film_id = ?";
    private static final String UPDATE_QUERY_ADD_LIKE = "INSERT INTO likes(film_id, user_id)" + "VALUES(?, ?)";
    private static final String DELETE_QUERY_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_POPULAR = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, r.name rating_name  " +
            "FROM film f " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "LEFT JOIN rating r ON f.rating_id = r.rating_id " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";
    private static final String GET_COMMON_QUERY =
            "SELECT f.*, r.name AS rating_name, COUNT(all_likes.user_id) AS popularity " +
                    "FROM film f " +
                    "JOIN likes l1 ON f.film_id = l1.film_id " +
                    "JOIN likes l2 ON f.film_id = l2.film_id " +
                    "JOIN likes all_likes ON f.film_id = all_likes.film_id " +
                    "LEFT JOIN rating r ON f.rating_id = r.rating_id " +
                    "WHERE l1.user_id = ? " +
                    "AND l2.user_id = ? " +
                    "GROUP BY f.film_id, r.name " +
                    "ORDER BY popularity DESC";
    private static final String GET_FILM_DIRECTOR_BY_YEAR =
            "SELECT f.*, r.name AS rating_name " +
                    "FROM film f " +
                    "JOIN film_director fd ON fd.film_id = f.film_id " +
                    "LEFT JOIN rating r ON f.rating_id = r.rating_id " +
                    "WHERE fd.director_id = ?" +
                    "ORDER BY f.releaseDate";
    private static final String GET_FILM_DIRECTOR_BY_LIKES =
            "SELECT f.*, r.name AS rating_name, COALESCE(lc.like_count, 0) AS likes_count " +
                    "FROM film f " +
                    "JOIN film_director fd ON fd.film_id = f.film_id " +
                    "LEFT JOIN rating r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN ( " +
                        "SELECT film_id, COUNT(user_id) AS like_count " +
                        "FROM likes " +
                        "GROUP BY film_id " +
                    ") AS lc ON lc.film_id = f.film_id " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY likes_count DESC";

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
        film.getDirectors().forEach(director -> setFilmDirectors(id, director.getId()));

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
        film.getDirectors().forEach(director -> setFilmDirectors(film.getId(), director.getId()));

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

    private void setFilmDirectors(Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_INTO_FILM_DIRECTORS, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }

            return ps;
        }, keyHolder);
    }

    public void deleteDirectors(int filmId) {
        jdbc.update(DELETE_FILM_DIRECTORS, filmId);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return findMany(GET_COMMON_QUERY, userId, friendId);
    }

    public List<Film> getDirectorFilmsByYear(int directorId) {
        return findMany(GET_FILM_DIRECTOR_BY_YEAR, directorId);
    }

    public List<Film> getDirectorFilmsByLikes(int directorId) {
        return findMany(GET_FILM_DIRECTOR_BY_LIKES, directorId);
    }
}
