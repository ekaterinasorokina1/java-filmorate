package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review create(Review review);

    void update(Review review);

    void delete(int reviewId);

    List<Review> getAll(int count);

    Optional<Review> get(int reviewId);

    List<Review> getAllFromFilm(int filmId, int count);

    void like(int reviewId, int userId);

    void dislike(int reviewId, int userId);

    void deleteLike(int reviewId, int userId);

    void deleteDislike(int reviewId, int userId);
}
