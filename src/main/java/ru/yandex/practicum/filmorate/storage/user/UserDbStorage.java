package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ? WHERE user_id = ?";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friends (user_id, friend_id) " +
            "VALUES (?, ?)";
    final String FRIEND_GET_QUERY = "SELECT * FROM users u " +
            "JOIN friends f " +
            "ON u.user_id = f.friend_id " +
            "WHERE f.user_id = ?";
    final String REMOVE_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    final String GET_COMMON_FRIENDS_QUERY = "SELECT * FROM users u " +
            "JOIN friends f1 ON u.user_id = f1.friend_id " +
            "JOIN friends f2 ON u.user_id = f2.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id = ?";


    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> getById(int userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public User create(User user) {
        int id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getId()
        );
        return user;
    }

    public void addFriend(int userId, int friendId) {
        update(ADD_FRIEND_QUERY, userId, friendId);
    }

    public List<User> getFriends(int userId) {
        return findMany(FRIEND_GET_QUERY, userId);
    }

    public void deleteFriend(int userId, int friendId) {
        jdbc.update(REMOVE_FRIEND_QUERY, userId, friendId);
    }

    public List<User> getCommonFriend(int userId, int otherId) {
        return findMany(GET_COMMON_FRIENDS_QUERY, userId, otherId);
    }
}
