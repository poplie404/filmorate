package filmorate.storage.film;

import filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Film add(Film film);

    public Film update(Film film);

    public Film getById(int id);

    void delete(int id);

    public Collection<Film> getAll();
}
