package filmorate.storage.film;

import filmorate.model.Film;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film add(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NoSuchElementException("Фильм с id " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(int id) {
        if (!films.containsKey(id)) {
            throw new NoSuchElementException("Фильм с id " + id + " не найден");
        }
        films.remove(id);
    }

    @Override
    public Film getById(int id) {
        if (!films.containsKey(id)) {
            throw new NoSuchElementException("Фильм с id " + id + " не найден");
        }
        return films.get(id);
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    private int getNextId() {
        return films.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1;
    }
}
