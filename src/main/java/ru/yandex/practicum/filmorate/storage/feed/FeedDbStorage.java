package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperationType;

import java.time.Instant;
import java.util.List;

@Repository
public class FeedDbStorage extends BaseRepository<Feed> implements FeedStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM feed WHERE user_id = ? ";
    private static final String ADD_QUERY = "INSERT INTO feed (user_id, event_date, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?)";

    public FeedDbStorage(JdbcTemplate jdbc, RowMapper<Feed> mapper) {
        super(jdbc, mapper);
    }

    public List<Feed> getAll(int userId) {
        return findMany(FIND_ALL_QUERY, userId);
    }

    public void add(int userId, FeedEventType eventType, FeedOperationType operation, int entityId) {
        update(ADD_QUERY, userId, Instant.now(), eventType.name(), operation.name(), entityId);
    }
}
