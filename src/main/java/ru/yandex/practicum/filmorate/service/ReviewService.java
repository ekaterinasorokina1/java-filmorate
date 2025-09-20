package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public ReviewDto create(NewReviewRequest request) {
        validateFilm(request.getFilmId());
        validateUser(request.getUserId());
        Review review = ReviewMapper.mapToReview(request);

        reviewStorage.create(review);
        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto update(UpdateReviewRequest request) {
        Review updatedReview = reviewStorage.get(request.getId())
                .map(review -> ReviewMapper.updateReviewFields(review, request))
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        reviewStorage.update(updatedReview);
        updatedReview = reviewStorage.get(updatedReview.getId()).get();

        return ReviewMapper.mapToReviewDto(updatedReview);
    }

    public void delete(int reviewId) {
        validateReview(reviewId);
        reviewStorage.delete(reviewId);
    }

    public ReviewDto get(int reviewId) {
        validateReview(reviewId);
        return reviewStorage.get(reviewId)
                .map(ReviewMapper::mapToReviewDto)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
    }

    public List<ReviewDto> getAll(int filmId, int count) {
        if (filmId == -1) {
            return reviewStorage.getAll(count).stream().
                    map(ReviewMapper::mapToReviewDto).collect(Collectors.toList());
        }
        else {
            validateFilm(filmId);
            return reviewStorage.getAllFromFilm(filmId, count).stream().
                    map(ReviewMapper::mapToReviewDto).collect(Collectors.toList());
        }
    }


    public void like(int reviewId, int userId) {
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.like(reviewId, userId);
    }

    public void dislike(int reviewId, int userId) {
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.dislike(reviewId, userId);
    }

    public void removeLike(int reviewId, int userId) {
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.deleteLike(reviewId, userId);
    }

    public void removeDislike(int reviewId, int userId) {
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.deleteDislike(reviewId, userId);
    }

    private void validateFilm(int filmId) {
        filmStorage.getById(filmId).orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
    }

    private void validateUser(int userId) {
        userStorage.getById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private void validateReview(int reviewId) {
        reviewStorage.get(reviewId).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
    }
}
