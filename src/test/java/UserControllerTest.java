
import filmorate.controllers.UserController;
import filmorate.exceptions.ValidationException;
import filmorate.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController();
    }

    private User createValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("login123");
        user.setName("John Doe");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }


    @Test
    void postUser_success() {
        User added = controller.postUser(createValidUser());
        assertNotNull(added.getId());
        assertEquals(1, controller.getUsers().size());
    }

    @Test
    void postUser_emptyEmail_shouldThrow() {
        User user = createValidUser();
        user.setEmail("");
        assertThrows(ValidationException.class, () -> controller.postUser(user));
    }

    @Test
    void postUser_invalidEmail_shouldThrow() {
        User user = createValidUser();
        user.setEmail("invalid-email");
        assertThrows(ValidationException.class, () -> controller.postUser(user));
    }

    @Test
    void postUser_emptyLogin_shouldThrow() {
        User user = createValidUser();
        user.setLogin("");
        assertThrows(ValidationException.class, () -> controller.postUser(user));
    }

    @Test
    void postUser_loginWithSpaces_shouldThrow() {
        User user = createValidUser();
        user.setLogin("bad login");
        assertThrows(ValidationException.class, () -> controller.postUser(user));
    }

    @Test
    void postUser_birthdayInFuture_shouldThrow() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> controller.postUser(user));
    }

    // ---------- UPDATE TESTS ----------

    @Test
    void updateUser_success() {
        User added = controller.postUser(createValidUser());

        User updated = createValidUser();
        updated.setId(added.getId());
        updated.setName("Updated Name");

        User result = controller.update(updated);

        assertEquals("Updated Name", result.getName());
        assertEquals(1, controller.getUsers().size());
    }

    @Test
    void updateUser_missingId_shouldThrow() {
        User updated = createValidUser();
        updated.setId(0);
        assertThrows(ValidationException.class, () -> controller.update(updated));
    }

    @Test
    void updateUser_nonExistentId_shouldThrow() {
        User updated = createValidUser();
        updated.setId(999);
        assertThrows(ValidationException.class, () -> controller.update(updated));
    }

    @Test
    void updateUser_invalidEmail_shouldThrow() {
        User added = controller.postUser(createValidUser());
        User updated = createValidUser();
        updated.setId(added.getId());
        updated.setEmail("bad-email");
        assertThrows(ValidationException.class, () -> controller.update(updated));
    }

    @Test
    void updateUser_invalidLogin_shouldThrow() {
        User added = controller.postUser(createValidUser());
        User updated = createValidUser();
        updated.setId(added.getId());
        updated.setLogin("with space");
        assertThrows(ValidationException.class, () -> controller.update(updated));
    }
}
