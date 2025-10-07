import filmorate.model.User;
import filmorate.mappers.UserRowMapper;
import filmorate.storage.user.UserDbStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@Import({UserDbStorage.class, UserRowMapper.class})
@ActiveProfiles("test")
class UserDbStorageTest {

    @Configuration// без конфигурации все тесты пропускаются
    static class TestConfig {
    }

    @Autowired
    private UserDbStorage userDbStorage;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setLogin("login123");
        user.setName("John Doe");
        user.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("Добавление пользователя сохраняет его в БД и присваивает id")
    void addUser_shouldSaveToDatabase() {
        User saved = userDbStorage.add(user);

        assertThat(saved.getId()).isGreaterThan(0);
        User fromDb = userDbStorage.getById(saved.getId());

        assertThat(fromDb.getEmail()).isEqualTo("test@example.com");
        assertThat(fromDb.getLogin()).isEqualTo("login123");
    }

    @Test
    @DisplayName("Обновление пользователя изменяет его данные в БД")
    void updateUser_shouldModifyExistingRecord() {
        User saved = userDbStorage.add(user);
        saved.setEmail("updated@mail.com");
        saved.setName("Updated Name");

        userDbStorage.update(saved);
        User updated = userDbStorage.getById(saved.getId());

        assertThat(updated.getEmail()).isEqualTo("updated@mail.com");
        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("Удаление пользователя удаляет запись из БД")
    void deleteUser_shouldRemoveFromDatabase() {
        User saved = userDbStorage.add(user);
        userDbStorage.delete(saved.getId());

        assertThrows(Exception.class, () -> userDbStorage.getById(saved.getId()));
    }

    @Test
    @DisplayName("Получение всех пользователей возвращает список")
    void getAllUsers_shouldReturnList() {
        userDbStorage.add(user);

        User another = new User();
        another.setEmail("second@example.com");
        another.setLogin("user2");
        another.setName("Jane Doe");
        another.setBirthday(LocalDate.of(1995, 5, 5));
        userDbStorage.add(another);

        Collection<User> users = userDbStorage.getAll();
        assertThat(users).hasSizeGreaterThanOrEqualTo(2);
    }
}
