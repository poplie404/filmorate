package filmorate.service;

import filmorate.model.Film;
import filmorate.model.Genre;
import filmorate.model.Mpa;
import filmorate.model.User;
import filmorate.storage.film.FilmStorage;
import filmorate.storage.genre.GenreDbStorage;
import filmorate.storage.mpa.MpaDbStorage;
import filmorate.storage.user.UserStorage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;

    public FilmService(@Qualifier("filmDbStorage")FilmStorage filmStorage, UserStorage userStorage,
                       GenreDbStorage genreStorage, MpaDbStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public Film add(Film film) {
        resolveGenresAndMpa(film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        resolveGenresAndMpa(film);
        return filmStorage.update(film);
    }

    public Film getById(int id) {
        Film film = filmStorage.getById(id);
        if (film == null) throw new NoSuchElementException("Фильм с ID " + id + " не найден");
        return film;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, int userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getMostPopular(int count) {
        return filmStorage.getMostPopular(count);
    }

    private void resolveGenresAndMpa(Film film) {
        if (film.getGenres() != null) {
            List<Genre> genres = film.getGenres().stream()
                    .map(g -> genreStorage.getById(g.getId()))
                    .distinct()
                    .sorted(Comparator.comparingInt(Genre::getId))
                    .collect(Collectors.toList());
            film.setGenres(genres);
        }
        if (film.getMpa() != null) {
            Mpa mpa = mpaStorage.getById(film.getMpa().getId());
            film.setMpa(mpa);
        }
    }


    private Film getFilmOrThrow(int id) {
        Film film = filmStorage.getById(id);
        if (film == null) throw new NoSuchElementException("Фильм с ID " + id + " не найден");
        return film;
    }

    private User getUserOrThrow(int id) {
        User user = userStorage.getById(id);
        if (user == null) throw new NoSuchElementException("Пользователь с ID " + id + " не найден");
        return user;
    }
}
