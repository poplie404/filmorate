package filmorate.storage.genre;

import filmorate.model.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(GenreDbStorage.class)
@ActiveProfiles("test")
public class GenreDbStorageTest {

    @org.springframework.context.annotation.Configuration
    static class TestConfig {
    }

    @Autowired
    private GenreDbStorage genreDbStorage;

    @Test
    void getById_shouldReturnCorrectGenre() {
        Genre genre = genreDbStorage.getById(1);

        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1);
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    void getAll_shouldReturnAllGenres() {
        Collection<Genre> genres = genreDbStorage.getAll();

        assertThat(genres).isNotEmpty();
        assertThat(genres).hasSize(6);
    }
}
