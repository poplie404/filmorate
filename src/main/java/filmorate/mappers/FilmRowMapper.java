package filmorate.mappers;

import filmorate.model.Film;
import filmorate.model.Mpa;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        int mpaId = rs.getInt("mpa_id");
        String mpaName = rs.getString("mpa_name");

        if (mpaId > 0 && mpaName != null) {
            Mpa mpa = new Mpa();
            mpa.setId(mpaId);
            mpa.setName(mpaName);
            film.setMpa(mpa);
        }

        return film;
    }
}
