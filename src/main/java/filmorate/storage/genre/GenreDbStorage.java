package filmorate.storage.genre;

import filmorate.exceptions.NotFoundException;
import filmorate.model.Genre;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_GENRES_BY_ID_SQL = "SELECT * FROM genres WHERE id = ?";
    private static final String SELECT_ALL_GENRES_SQL = "SELECT * FROM genres";

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getById(int id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_GENRES_BY_ID_SQL, (rs, rowNum) ->
                    new Genre(rs.getInt("id"), rs.getString("name")), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с id " + id + " не найден");
        }
    }

    public List<Genre> getAll() {
        return jdbcTemplate.query(SELECT_ALL_GENRES_SQL, (rs, rowNum) ->
                new Genre(rs.getInt("id"), rs.getString("name")));
    }
}
