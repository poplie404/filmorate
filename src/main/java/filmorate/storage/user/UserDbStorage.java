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

    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public User add(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?";
        int updated = jdbcTemplate.update(sql,
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
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public void addFriend(int userId, int friendId) {
        if (!exists(userId) || !exists(friendId)) {
            throw new NotFoundException("Один из пользователей не найден");
        }

        try {
            String sql = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, userId, friendId);
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка добавления друга", e);
        }
    }

    public void removeFriend(int userId, int friendId) {
        if (!exists(userId) || !exists(friendId)) {
            throw new NotFoundException("Один из пользователей не найден");
        }

        String sql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        // не бросаем NotFoundException, даже если updated == 0
    }

    public List<User> getFriends(int userId) {
        if (!exists(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        String sql = """
                SELECT u.* FROM users u
                JOIN user_friends uf ON u.id = uf.friend_id
                WHERE uf.user_id = ?
                """;
        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        if (!exists(userId) || !exists(otherId)) {
            throw new NotFoundException("Один из пользователей не найден");
        }

        String sql = """
        SELECT u.*
        FROM users u
        JOIN user_friends uf1 ON u.id = uf1.friend_id
        JOIN user_friends uf2 ON u.id = uf2.friend_id
        WHERE uf1.user_id = ? AND uf2.user_id = ?
        """;

        return jdbcTemplate.query(sql, userRowMapper, userId, otherId);
    }


    public boolean exists(int id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }


}
