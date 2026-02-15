package filmorate.storage.film;

import filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Film getById(int id);

    void delete(int id);

    Collection<Film> getAll();
}
