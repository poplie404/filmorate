package filmorate.storage.mpa;

import filmorate.exceptions.NotFoundException;
import filmorate.model.Mpa;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository("MpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_MPA_BY_ID_SQL = "SELECT * FROM mpa WHERE id = ?";

    private static final String SELECT_ALL_MPA_ORDERED_SQL = "SELECT * FROM mpa ORDER BY id";

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getById(int id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_MPA_BY_ID_SQL, (rs, rowNum) ->
                    new Mpa(rs.getInt("id"), rs.getString("name")), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("MPA с id " + id + " не найден");
        }
    }


    @Override
    public Collection<Mpa> getAll() {
        return jdbcTemplate.query(SELECT_ALL_MPA_ORDERED_SQL, (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        });
    }
}
