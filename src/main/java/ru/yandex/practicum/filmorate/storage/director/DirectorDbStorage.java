package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Director;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDbStorage extends BaseRepository<Director> implements DirectorStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM director";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM director WHERE director_id = ?";
    private static final String FIND_FILM_DIRECTORS_BY_ID = "SELECT d.director_id AS director_id, " +
            "d.name AS director_name " +
            "FROM director AS d " +
            "INNER JOIN film_director AS fd ON d.director_id = fd.director_id " +
            "WHERE fd.film_id = ? " +
            "ORDER BY d.director_id";
    private static final String INSERT_QUERY = "INSERT INTO director (name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE director SET name = ? WHERE director_id = ?";
    private static final String DELETE = "DELETE FROM director WHERE director_id = ?";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public List<Director> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Director> getById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Director> getFilmDirectors(int filmId) {
        return jdbc.query(FIND_FILM_DIRECTORS_BY_ID, mapper, filmId);
    }

    public Director create(Director director) {
        int id = insert(
                INSERT_QUERY,
                director.getName()
        );
        director.setId(id);

        return director;
    }

    public Director update(Director director) {
        update(
                UPDATE_QUERY,
                director.getName(),
                director.getId()
        );

        return director;
    }

    public void deleteById(int id) {
        delete(DELETE, id);
    }
}