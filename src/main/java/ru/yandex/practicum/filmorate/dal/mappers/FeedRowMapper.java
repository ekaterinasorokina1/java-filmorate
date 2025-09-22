package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Feed feed = new Feed();
        feed.setEventId(resultSet.getInt("event_id"));
        feed.setTimestamp(resultSet.getTimestamp("event_date").toInstant().toEpochMilli());
        feed.setUserId(resultSet.getInt("user_id"));
        feed.setEventType(FeedEventType.valueOf(resultSet.getString("event_type")));
        feed.setOperation(FeedOperationType.valueOf(resultSet.getString("operation")));
        feed.setEntityId(resultSet.getInt("entity_id"));
        return feed;
    }
}