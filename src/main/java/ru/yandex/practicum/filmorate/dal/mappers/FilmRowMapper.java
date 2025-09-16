package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setDuration(resultSet.getInt("duration"));
        Rating rating = new Rating();
        rating.setId(resultSet.getInt("rating_id"));
        rating.setName(resultSet.getString("rating_name"));
        film.setRating(rating);

        Timestamp releaseDate = resultSet.getTimestamp("releaseDate");
        film.setReleaseDate(releaseDate.toLocalDateTime().toLocalDate());

        return film;
    }
}
