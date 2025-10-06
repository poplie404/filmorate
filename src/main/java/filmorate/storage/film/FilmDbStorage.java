package filmorate.storage.film;

import filmorate.exceptions.NotFoundException;
import filmorate.mappers.FilmRowMapper;
import filmorate.model.Film;
import filmorate.model.Genre;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
    }

    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null
        );

        Integer id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM films", Integer.class);
        film.setId(id);

        // Сохраняем жанры
        addGenresToFilm(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE id=?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        // Обновляем жанры
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id=?", film.getId());
        addGenresToFilm(film);

        return getById(film.getId());
    }

    @Override
    public Film getById(int id) {
        String sql = "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name " +
                "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id WHERE f.id=?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, id);
        if (films.isEmpty()) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }

        Film film = films.get(0);
        film.setGenres(loadGenres(film.getId()));
        film.setLikes(loadLikes(film.getId()));
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        for (Film film : films) {
            film.setGenres(loadGenres(film.getId()));
            film.setLikes(loadLikes(film.getId()));
        }
        return films;
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM films WHERE id=?", id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlCheck = "SELECT COUNT(*) FROM film_likes WHERE film_id=? AND user_id=?";
        Integer count = jdbcTemplate.queryForObject(sqlCheck, Integer.class, filmId, userId);
        if (count != null && count == 0) {
            jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id=? AND user_id=?", filmId, userId);
    }

    @Override
    public List<Film> getMostPopular(int count) {
        String sql = "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "GROUP BY f.id, m.id, m.name " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);
        for (Film film : films) {
            film.setGenres(loadGenres(film.getId()));
            film.setLikes(loadLikes(film.getId()));
        }
        return films;
    }

    private void addGenresToFilm(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) return;

        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        film.getGenres().stream()
                .sorted(Comparator.comparingInt(Genre::getId))
                .forEach(genre -> jdbcTemplate.update(sql, film.getId(), genre.getId()));
    }


    private List<Genre> loadGenres(int filmId) {
        String sql = """
        SELECT g.id, g.name 
        FROM genres g
        JOIN film_genres fg ON g.id = fg.genre_id
        WHERE fg.film_id = ?
        ORDER BY g.id
        """;
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")),
                filmId);
    }

    private Set<Integer> loadLikes(int filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id=?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), filmId));
    }
}
