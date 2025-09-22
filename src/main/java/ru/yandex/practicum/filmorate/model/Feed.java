package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Feed {
    @NotNull
    int eventId;

    @NotNull
    Long timestamp;

    @NotNull
    int userId;

    @NotNull
    FeedEventType eventType;

    @NotNull
    FeedOperationType operation;

    @NotNull
    int entityId;
}
