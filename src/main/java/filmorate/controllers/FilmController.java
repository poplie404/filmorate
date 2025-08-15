package filmorate.controllers;

import filmorate.exceptions.ValidationException;
import filmorate.model.Film;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/films")
public class FilmController {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Получен запрос: список всех фильмов ({} шт.)", films.size());
        return films.values();
    }

    @PostMapping
    public Film postFilms(@RequestBody Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == 0) {
            log.error("Ошибка обновления: id не указан");
            throw new ValidationException("Ошибка обновления: id должен быть указан");
        }
        if (!films.containsKey(newFilm.getId())) {
            log.error("Ошибка обновления: фильм с id {} не найден", newFilm.getId());
            throw new ValidationException("Ошибка обновления: фильм с id " + newFilm.getId() + " не найден");
        }
        validateFilm(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм обновлён: {}", newFilm);
        return newFilm;
    }

    private int getNextId() {
        return films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка валидации: пустое имя фильма");
            throw new ValidationException("Ошибка: имя фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Ошибка валидации: описание длиной {} символов", film.getDescription().length());
            throw new ValidationException("Ошибка: описание не может быть более 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_DATE)) {
            log.error("Ошибка валидации: дата {} раньше допустимой {}", film.getReleaseDate(), MIN_DATE);
            throw new ValidationException("Ошибка: дата фильма не может быть ранее 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            log.error("Ошибка валидации: некорректная продолжительность {}", film.getDuration());
            throw new ValidationException("Ошибка: продолжительность должна быть положительным числом");
        }
    }
}
