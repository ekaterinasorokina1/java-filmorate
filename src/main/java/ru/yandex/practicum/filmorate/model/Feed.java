package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Feed {
    @NotNull
    private int eventId;

    @NotNull
    private Long timestamp;

    @NotNull
    private int userId;

    @NotNull
    private FeedEventType eventType;

    @NotNull
    private FeedOperationType operation;

    @NotNull
    private int entityId;
}
