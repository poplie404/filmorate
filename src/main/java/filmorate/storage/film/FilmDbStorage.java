package filmorate.storage.film;

import filmorate.exceptions.NotFoundException;
import filmorate.mappers.FilmRowMapper;
import filmorate.model.Film;
import filmorate.model.Genre;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    private static final String INSERT_FILM_SQL = """
        INSERT INTO films (name, description, release_date, duration, mpa_id)
        VALUES (?, ?, ?, ?, ?)
    """;

    private static final String UPDATE_FILM_SQL = """
        UPDATE films
        SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
        WHERE id = ?
    """;

    private static final String DELETE_FILM_SQL = """
        DELETE FROM films WHERE id = ?
    """;

    private static final String SELECT_MAX_FILM_ID_SQL = """
        SELECT MAX(id) FROM films
    """;

    private static final String SELECT_FILM_BY_ID_SQL = """
        SELECT f.*, m.id AS mpa_id, m.name AS mpa_name
        FROM films f
        LEFT JOIN mpa m ON f.mpa_id = m.id
        WHERE f.id = ?
    """;

    private static final String SELECT_ALL_FILMS_SQL = """
        SELECT f.*, m.id AS mpa_id, m.name AS mpa_name
        FROM films f
        LEFT JOIN mpa m ON f.mpa_id = m.id
    """;

    private static final String INSERT_FILM_GENRE_SQL = """
        INSERT INTO film_genres (film_id, genre_id)
        VALUES (?, ?)
    """;

    private static final String DELETE_FILM_GENRES_SQL = """
        DELETE FROM film_genres WHERE film_id = ?
    """;

    private static final String SELECT_GENRES_BY_FILM_SQL = """
        SELECT g.id, g.name
        FROM genres g
        JOIN film_genres fg ON g.id = fg.genre_id
        WHERE fg.film_id = ?
        ORDER BY g.id
    """;

    private static final String SELECT_LIKES_BY_FILM_SQL = """
        SELECT user_id FROM film_likes WHERE film_id = ?
    """;

    private static final String CHECK_LIKE_EXISTS_SQL = """
        SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?
    """;

    private static final String INSERT_LIKE_SQL = """
        INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)
    """;

    private static final String DELETE_LIKE_SQL = """
        DELETE FROM film_likes WHERE film_id = ? AND user_id = ?
    """;

    private static final String SELECT_MOST_POPULAR_SQL = """
        SELECT f.*, m.id AS mpa_id, m.name AS mpa_name
        FROM films f
        LEFT JOIN mpa m ON f.mpa_id = m.id
        LEFT JOIN film_likes fl ON f.id = fl.film_id
        GROUP BY f.id, m.id, m.name
        ORDER BY COUNT(fl.user_id) DESC
        LIMIT ?
    """;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
    }

    @Override
    public Film add(Film film) {
        jdbcTemplate.update(INSERT_FILM_SQL,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null
        );

        Integer id = jdbcTemplate.queryForObject(SELECT_MAX_FILM_ID_SQL, Integer.class);
        film.setId(id);

        addGenresToFilm(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update(UPDATE_FILM_SQL,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        jdbcTemplate.update(DELETE_FILM_GENRES_SQL, film.getId());
        addGenresToFilm(film);

        return getById(film.getId());
    }

    @Override
    public Film getById(int id) {
        List<Film> films = jdbcTemplate.query(SELECT_FILM_BY_ID_SQL, filmRowMapper, id);
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
        List<Film> films = jdbcTemplate.query(SELECT_ALL_FILMS_SQL, filmRowMapper);
        for (Film film : films) {
            film.setGenres(loadGenres(film.getId()));
            film.setLikes(loadLikes(film.getId()));
        }
        return films;
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(DELETE_FILM_SQL, id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Integer count = jdbcTemplate.queryForObject(CHECK_LIKE_EXISTS_SQL, Integer.class, filmId, userId);
        if (count != null && count == 0) {
            jdbcTemplate.update(INSERT_LIKE_SQL, filmId, userId);
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update(DELETE_LIKE_SQL, filmId, userId);
    }

    @Override
    public List<Film> getMostPopular(int count) {
        List<Film> films = jdbcTemplate.query(SELECT_MOST_POPULAR_SQL, filmRowMapper, count);
        for (Film film : films) {
            film.setGenres(loadGenres(film.getId()));
            film.setLikes(loadLikes(film.getId()));
        }
        return films;
    }

    private void addGenresToFilm(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) return;

        film.getGenres().stream()
                .sorted(Comparator.comparingInt(Genre::getId))
                .forEach(genre -> jdbcTemplate.update(INSERT_FILM_GENRE_SQL, film.getId(), genre.getId()));
    }

    private List<Genre> loadGenres(int filmId) {
        return jdbcTemplate.query(SELECT_GENRES_BY_FILM_SQL,
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")),
                filmId);
    }

    private Set<Integer> loadLikes(int filmId) {
        return new HashSet<>(jdbcTemplate.query(SELECT_LIKES_BY_FILM_SQL,
                (rs, rowNum) -> rs.getInt("user_id"),
                filmId));
    }
}
