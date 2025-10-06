import filmorate.mappers.FilmRowMapper;
import filmorate.model.Film;
import filmorate.model.Mpa;
import filmorate.storage.film.FilmDbStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({FilmDbStorage.class, FilmRowMapper.class})
@ActiveProfiles("test")
class FilmDbStorageTest {

    @org.springframework.context.annotation.Configuration
    static class TestConfig {
    }

    @Autowired
    private FilmDbStorage filmDbStorage;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Inception");
        testFilm.setDescription("A mind-bending thriller by Nolan");
        testFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        testFilm.setDuration(148);
        testFilm.setMpa(new Mpa(1, "G")); // в data.sql есть рейтинг с id=1
    }

    @Test
    void addFilm_success() {
        Film saved = filmDbStorage.add(testFilm);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isPositive();

        Film fromDb = filmDbStorage.getById(saved.getId());
        assertThat(fromDb.getName()).isEqualTo("Inception");
        assertThat(fromDb.getDescription()).contains("Nolan");
        assertThat(fromDb.getMpa().getId()).isEqualTo(1);
    }

    @Test
    void updateFilm_success() {
        Film saved = filmDbStorage.add(testFilm);
        saved.setName("Updated name");
        saved.setDescription("Updated description");
        saved.setDuration(155);

        Film updated = filmDbStorage.update(saved);
        assertThat(updated.getName()).isEqualTo("Updated name");
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getDuration()).isEqualTo(155);

        Film fromDb = filmDbStorage.getById(saved.getId());
        assertThat(fromDb.getName()).isEqualTo("Updated name");
    }

    @Test
    void getAllFilms_shouldReturnList() {
        filmDbStorage.add(testFilm);

        Film second = new Film();
        second.setName("Interstellar");
        second.setDescription("Another Nolan film");
        second.setReleaseDate(LocalDate.of(2014, 11, 7));
        second.setDuration(169);
        second.setMpa(new Mpa(2, "PG"));

        filmDbStorage.add(second);

        List<Film> films = (List<Film>) filmDbStorage.getAll();
        assertThat(films).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deleteFilm_shouldRemoveFromDb() {
        Film saved = filmDbStorage.add(testFilm);

        filmDbStorage.delete(saved.getId());

        Optional<Film> deleted = filmDbStorage.getAll()
                .stream()
                .filter(f -> f.getId() == saved.getId())
                .findFirst();

        assertThat(deleted).isEmpty();
    }
}
