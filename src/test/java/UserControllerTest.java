

import com.fasterxml.jackson.databind.ObjectMapper;
import filmorate.FilmorateApplication;
import filmorate.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User createValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("login123");
        user.setName("John Doe");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    @BeforeEach
    void reset() throws Exception {
        // если у тебя есть эндпоинт очистки пользователей — лучше использовать его
        // или просто следи за id в тестах
    }

    @Test
    void postUser_success() throws Exception {
        User valid = createValidUser();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valid)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.login").value("login123"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.birthday").value("2000-01-01"));
    }

    @Test
    void postUser_invalidEmail_shouldFail() throws Exception {
        User user = createValidUser();
        user.setEmail("bad-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postUser_blankLogin_shouldFail() throws Exception {
        User user = createValidUser();
        user.setLogin("  "); // пробелы

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postUser_futureBirthday_shouldFail() throws Exception {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1)); // завтрашний день

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsers_shouldReturnList() throws Exception {
        User valid = createValidUser();

        // добавляем пользователя
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valid)))
                .andExpect(status().isOk());

        // проверяем, что список не пустой
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }
}
