package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {
    private static final String INSERT_QUERY = "INSERT INTO review (content, positive, user_id, film_id) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE review SET content = ?, positive = ? WHERE review_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM review WHERE review_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM review LIMIT ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM review WHERE review_id = ?";
    private static final String FIND_BY_FILM_QUERY = "SELECT * FROM review WHERE film_id = ? LIMIT ?";
    private static final String LIKE_QUERY = "INSERT INTO review_like (review_id, user_id, is_like) VALUES (?, ?, true)";
    private static final String DISLIKE_QUERY = "INSERT INTO review_like (review_id, user_id, is_like) VALUES (?, ?, false)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM review_like WHERE review_id = ? AND user_id = ? AND is_like = true";
    private static final String DELETE_DISLIKE_QUERY = "DELETE FROM review_like WHERE review_id = ? AND user_id = ? AND is_like = false";


    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    public Review create(Review review) {
        int id = insert(INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId());
        review.setId(id);
        return review;
    }

    public void update(Review review) {
        update(UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getId());
    }

    public void delete(int reviewId) {
        delete(DELETE_QUERY, reviewId);
    }

    public List<Review> getAll(int count) {
        return findMany(FIND_ALL_QUERY, count);
    }

    public Optional<Review> get(int reviewId) {
        return findOne(FIND_BY_ID_QUERY, reviewId);
    }

    public List<Review> getAllFromFilm(int filmId, int count) {
        return findMany(FIND_BY_FILM_QUERY, filmId, count);
    }

    public void like(int reviewId, int userId) {
        insert(LIKE_QUERY, reviewId, userId);
    }

    public void dislike(int reviewId, int userId) {
        insert(DISLIKE_QUERY, reviewId, userId);
    }

    public void deleteLike(int reviewId, int userId) {
        delete(DELETE_LIKE_QUERY, reviewId, userId);
    }

    public void deleteDislike(int reviewId, int userId) {
        delete(DELETE_DISLIKE_QUERY, reviewId, userId);
    }
}
