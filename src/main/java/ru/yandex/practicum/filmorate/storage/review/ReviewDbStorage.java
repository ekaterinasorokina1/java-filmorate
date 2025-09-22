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
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM review WHERE review_id = ?";
    private static final String FIND_BY_FILM_QUERY = "SELECT * FROM review WHERE (? IS NULL OR film_id = ?) ORDER BY useful DESC LIMIT ?;";
    private static final String LIKE_QUERY = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, true)";
    private static final String DISLIKE_QUERY = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, false)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = true";
    private static final String DELETE_DISLIKE_QUERY = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = false";


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

    public Optional<Review> get(int reviewId) {
        return findOne(FIND_BY_ID_QUERY, reviewId);
    }

    public List<Review> getAllFromFilm(Integer filmId, int count) {
        return findMany(FIND_BY_FILM_QUERY, filmId, filmId, count);
    }

    public void like(int reviewId, int userId) {
        deleteDislike(reviewId, userId);
        jdbc.update(LIKE_QUERY, reviewId, userId);
        updateUsefulCount(reviewId);
    }

    public void dislike(int reviewId, int userId) {
        deleteLike(reviewId, userId);
        jdbc.update(DISLIKE_QUERY, reviewId, userId);
        updateUsefulCount(reviewId);
    }

    public void deleteLike(int reviewId, int userId) {
        delete(DELETE_LIKE_QUERY, reviewId, userId);
        updateUsefulCount(reviewId);
    }

    public void deleteDislike(int reviewId, int userId) {
        delete(DELETE_DISLIKE_QUERY, reviewId, userId);
        updateUsefulCount(reviewId);
    }

    private void updateUsefulCount(int reviewId) {
        jdbc.update("UPDATE review SET useful = (" +
                "SELECT COALESCE(SUM(CASE WHEN is_like = true THEN 1 ELSE -1 END), 0) " +
                "FROM review_likes WHERE review_id = ?" +
                ") WHERE review_id = ?", reviewId, reviewId);
    }
}
