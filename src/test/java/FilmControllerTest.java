
import filmorate.controllers.FilmController;
import filmorate.exceptions.ValidationException;
import filmorate.model.Film;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
    }

    private Film createValidFilm() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        return film;
    }


    @Test
    void postFilms_success() {
        Film added = controller.postFilms(createValidFilm());
        assertNotNull(added.getId());
        assertEquals(1, controller.getFilms().size());
    }

    @Test
    void postFilms_emptyName_shouldThrow() {
        Film film = createValidFilm();
        film.setName("");
        assertThrows(ValidationException.class, () -> controller.postFilms(film));
    }

    @Test
    void postFilms_nullName_shouldThrow() {
        Film film = createValidFilm();
        film.setName(null);
        assertThrows(ValidationException.class, () -> controller.postFilms(film));
    }

    @Test
    void postFilms_tooLongDescription_shouldThrow() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, () -> controller.postFilms(film));
    }

    @Test
    void postFilms_releaseDateTooEarly_shouldThrow() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        assertThrows(ValidationException.class, () -> controller.postFilms(film));
    }

    @Test
    void postFilms_negativeDuration_shouldThrow() {
        Film film = createValidFilm();
        film.setDuration(-1);
        assertThrows(ValidationException.class, () -> controller.postFilms(film));
    }

    @Test
    void postFilms_zeroDuration_shouldThrow() {
        Film film = createValidFilm();
        film.setDuration(0);
        assertThrows(ValidationException.class, () -> controller.postFilms(film));
    }


    @Test
    void updateFilm_success() {
        Film added = controller.postFilms(createValidFilm());

        Film updated = createValidFilm();
        updated.setId(added.getId());
        updated.setName("Updated");
        updated.setDescription("New description");
        updated.setReleaseDate(LocalDate.of(2010, 5, 5));
        updated.setDuration(150);

        Film result = controller.update(updated);

        assertEquals("Updated", result.getName());
        assertEquals(1, controller.getFilms().size());
    }

    @Test
    void updateFilm_missingId_shouldThrow() {
        Film updated = createValidFilm();
        updated.setId(0);
        assertThrows(ValidationException.class, () -> controller.update(updated));
    }

    @Test
    void updateFilm_nonExistentId_shouldThrow() {
        Film updated = createValidFilm();
        updated.setId(999);
        assertThrows(ValidationException.class, () -> controller.update(updated));
    }

    @Test
    void updateFilm_invalidName_shouldThrow() {
        Film added = controller.postFilms(createValidFilm());
        Film updated = createValidFilm();
        updated.setId(added.getId());
        updated.setName("");
        assertThrows(ValidationException.class, () -> controller.update(updated));
    }

    @Test
    void updateFilm_tooLongDescription_shouldThrow() {
        Film added = controller.postFilms(createValidFilm());
        Film updated = createValidFilm();
        updated.setId(added.getId());
        updated.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, () -> controller.update(updated));
    }

    @Test
    void updateFilm_releaseDateTooEarly_shouldThrow() {
        Film added = controller.postFilms(createValidFilm());
        Film updated = createValidFilm();
        updated.setId(added.getId());
        updated.setReleaseDate(LocalDate.of(1800, 1, 1));
        assertThrows(ValidationException.class, () -> controller.update(updated));
    }

    @Test
    void updateFilm_invalidDuration_shouldThrow() {
        Film added = controller.postFilms(createValidFilm());
        Film updated = createValidFilm();
        updated.setId(added.getId());
        updated.setDuration(0);
        assertThrows(ValidationException.class, () -> controller.update(updated));
    }
}
