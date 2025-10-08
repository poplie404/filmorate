import filmorate.FilmorateApplication;
import filmorate.model.Mpa;
import filmorate.storage.mpa.MpaDbStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FilmorateApplication.class)
@ActiveProfiles("test")
class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage mpaDbStorage;

    @Test
    void getById_shouldReturnCorrectMpa() {
        Mpa mpa = mpaDbStorage.getById(1);

        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(1);
        assertThat(mpa.getName()).isEqualTo("G"); // есть в data.sql
    }

    @Test
    void getAll_shouldReturnAllMpaValues() {
        Collection<Mpa> mpaList = mpaDbStorage.getAll();

        assertThat(mpaList).isNotEmpty();
        assertThat(mpaList).hasSize(5); // у тебя в data.sql 5 записей
    }
}
