package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperationType;

import java.util.List;

public interface FeedStorage {
    List<Feed> getAll(int userId);

    void add(int userId, FeedEventType eventType, FeedOperationType operation, int entityId);
}