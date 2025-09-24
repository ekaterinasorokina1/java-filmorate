package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperationType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
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
    private final FeedStorage feedStorage;

    public ReviewDto create(NewReviewRequest request) {
        log.info("Создание отзыва: {}", request);
        validateFilm(request.getFilmId());
        validateUser(request.getUserId());
        Review review = ReviewMapper.mapToReview(request);
        review = reviewStorage.create(review);
        log.info("Отзыв создан с ID: {}", review.getId());

        feedStorage.add(review.getUserId(), FeedEventType.REVIEW, FeedOperationType.ADD, review.getId());

        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto update(UpdateReviewRequest request) {
        log.info("Обновление отзыва с ID: {}", request.getReviewId());
        Review updatedReview = reviewStorage.get(request.getReviewId())
                .map(review -> ReviewMapper.updateReviewFields(review, request))
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        reviewStorage.update(updatedReview);
        updatedReview = reviewStorage.get(updatedReview.getId()).get();
        log.debug("Отзыв с ID {} обновлен", updatedReview.getId());

        feedStorage.add(updatedReview.getUserId(), FeedEventType.REVIEW, FeedOperationType.UPDATE, updatedReview.getId());

        return ReviewMapper.mapToReviewDto(updatedReview);
    }

    public void delete(int reviewId) {
        log.info("Удаление отзыва с ID: {}", reviewId);

        Review review = reviewStorage.get(reviewId).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        feedStorage.add(review.getUserId(), FeedEventType.REVIEW, FeedOperationType.REMOVE, reviewId);

        reviewStorage.delete(reviewId);
        log.debug("Отзыв с ID {} удален", reviewId);
    }

    public ReviewDto get(int reviewId) {
        log.info("Получение отзыва с ID: {}", reviewId);
        validateReview(reviewId);
        return reviewStorage.get(reviewId)
                .map(ReviewMapper::mapToReviewDto)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
    }

    public List<ReviewDto> getAll(Integer filmId, int count) {
        log.info("Получение всех отзывов для фильма ID: {} с ограничением в {} отзывов", filmId, count);
        if (filmId != null) {
            validateFilm(filmId);
        }
        return reviewStorage.getAllFromFilm(filmId, count).stream()
                .map(ReviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public void like(int reviewId, int userId) {
        log.info("Добавление лайка от пользователя ID {} к отзыву ID {}", userId, reviewId);
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.like(reviewId, userId);
        log.debug("Лайк добавлен к отзыву ID {} от пользователя ID {}", reviewId, userId);
    }

    public void dislike(int reviewId, int userId) {
        log.info("Добавление дизлайка от пользователя ID {} к отзыву ID {}", userId, reviewId);
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.dislike(reviewId, userId);
        log.debug("Дизлайк добавлен к отзыву ID {} от пользователя ID {}", reviewId, userId);
    }

    public void removeLike(int reviewId, int userId) {
        log.info("Удаление лайка у отзыва ID {} от пользователя ID {}", reviewId, userId);
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.deleteLike(reviewId, userId);
        log.debug("Лайк удален у отзыва ID {} от пользователя ID {}", reviewId, userId);
    }

    public void removeDislike(int reviewId, int userId) {
        log.info("Удаление дизлайка у отзыва ID {} от пользователя ID {}", reviewId, userId);
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.deleteDislike(reviewId, userId);
        log.debug("Дизлайк удален у отзыва ID {} от пользователя ID {}", reviewId, userId);
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