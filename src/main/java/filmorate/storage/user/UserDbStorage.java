package filmorate.storage.user;

import filmorate.exceptions.NotFoundException;
import filmorate.mappers.UserRowMapper;
import filmorate.model.User;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Repository
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    private static final String INSERT_USER_SQL = """
        INSERT INTO users (email, login, name, birthday)
        VALUES (?, ?, ?, ?)
    """;

    private static final String UPDATE_USER_SQL = """
        UPDATE users
        SET email = ?, login = ?, name = ?, birthday = ?
        WHERE id = ?
    """;

    private static final String DELETE_USER_SQL = """
        DELETE FROM users WHERE id = ?
    """;

    private static final String SELECT_USER_BY_ID_SQL = """
        SELECT * FROM users WHERE id = ?
    """;

    private static final String SELECT_ALL_USERS_SQL = """
        SELECT * FROM users
    """;

    private static final String INSERT_FRIEND_SQL = """
        INSERT INTO user_friends (user_id, friend_id)
        VALUES (?, ?)
    """;

    private static final String DELETE_FRIEND_SQL = """
        DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?
    """;

    private static final String SELECT_FRIENDS_SQL = """
        SELECT u.*
        FROM users u
        JOIN user_friends uf ON u.id = uf.friend_id
        WHERE uf.user_id = ?
    """;

    private static final String SELECT_COMMON_FRIENDS_SQL = """
        SELECT u.*
        FROM users u
        JOIN user_friends uf1 ON u.id = uf1.friend_id
        JOIN user_friends uf2 ON u.id = uf2.friend_id
        WHERE uf1.user_id = ? AND uf2.user_id = ?
    """;

    private static final String CHECK_USER_EXISTS_SQL = """
        SELECT COUNT(*) FROM users WHERE id = ?
    """;

    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public User add(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User update(User user) {
        int updated = jdbcTemplate.update(UPDATE_USER_SQL,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );

        if (updated == 0) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        return getById(user.getId());
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(DELETE_USER_SQL, id);
    }

    @Override
    public User getById(int id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_USER_BY_ID_SQL, userRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query(SELECT_ALL_USERS_SQL, userRowMapper);
    }

    public void addFriend(int userId, int friendId) {
        if (!exists(userId) || !exists(friendId)) {
            throw new NotFoundException("Один из пользователей не найден");
        }

        try {
            jdbcTemplate.update(INSERT_FRIEND_SQL, userId, friendId);
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка добавления друга", e);
        }
    }

    public void removeFriend(int userId, int friendId) {
        if (!exists(userId) || !exists(friendId)) {
            throw new NotFoundException("Один из пользователей не найден");
        }

        jdbcTemplate.update(DELETE_FRIEND_SQL, userId, friendId);
    }

    public List<User> getFriends(int userId) {
        if (!exists(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        return jdbcTemplate.query(SELECT_FRIENDS_SQL, userRowMapper, userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        if (!exists(userId) || !exists(otherId)) {
            throw new NotFoundException("Один из пользователей не найден");
        }

        return jdbcTemplate.query(SELECT_COMMON_FRIENDS_SQL, userRowMapper, userId, otherId);
    }

    public boolean exists(int id) { //Используется в UserService.java
        Integer count = jdbcTemplate.queryForObject(CHECK_USER_EXISTS_SQL, Integer.class, id);
        return count != null && count > 0;
    }
}
