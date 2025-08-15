package filmorate.controllers;

import filmorate.exceptions.ValidationException;
import filmorate.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Получен запрос: список всех пользователей ({} шт.)", users.size());
        return users.values();
    }

    @PostMapping
    public User postUser(@RequestBody User user) {
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == 0) {
            log.error("Ошибка обновления: id не указан");
            throw new ValidationException("Ошибка обновления: id должен быть указан");
        }
        if (!users.containsKey(newUser.getId())) {
            log.error("Ошибка обновления: пользователь с id {} не найден", newUser.getId());
            throw new ValidationException("Ошибка обновления: пользователь с id " + newUser.getId() + " не найден");
        }
        validateUser(newUser);
        users.put(newUser.getId(), newUser);
        log.info("Пользователь обновлён: {}", newUser);
        return newUser;
    }

    private int getNextId() {
        return users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации: некорректный email '{}'", user.getEmail());
            throw new ValidationException("Ошибка: email не может быть пустым и должен содержать '@'");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Ошибка валидации: некорректный логин '{}'", user.getLogin());
            throw new ValidationException("Ошибка: логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя пустое — будет использован логин '{}'", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации: дата рождения {} в будущем", user.getBirthday());
            throw new ValidationException("Ошибка: дата рождения не может быть в будущем");
        }
    }
}
