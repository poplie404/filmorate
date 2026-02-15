package filmorate.storage.film;

import filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Film getById(int id);

    Collection<Film> getAll();

    void delete(int id);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getMostPopular(int count);
}
