package filmorate.service;

import filmorate.model.Film;
import filmorate.model.User;
import filmorate.storage.film.FilmStorage;
import filmorate.storage.user.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getById(int id) {
        return filmStorage.getById(id);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, int userId) {
        Film film = getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        film.getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        film.getLikes().remove(userId);
    }

    public List<Film> getMostPopular(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }

    private Film getFilmOrThrow(int id) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            throw new NoSuchElementException("Фильм с ID " + id + " не найден");
        }
        return film;
    }

    private User getUserOrThrow(int id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new NoSuchElementException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

}
