package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ReviewDto add(NewReviewRequest request) {
        return reviewService.create(request);
    }

    @PutMapping
    public ReviewDto update(UpdateReviewRequest request) {
        return reviewService.update(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public ReviewDto get(@PathVariable Integer id) {
        return reviewService.get(id);
    }

    @GetMapping
    public List<ReviewDto> getAll(@RequestParam(required = false, defaultValue = "-1") Integer filmId,
                                  @RequestParam(required = false, defaultValue = "10") Integer count) {
        return reviewService.getAll(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.like(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.dislike(id, userId);

    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.removeLike(id, userId);
  
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.removeDislike(id, userId);
    }
}
