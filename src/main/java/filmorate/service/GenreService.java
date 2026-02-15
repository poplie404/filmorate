package filmorate.service;

import filmorate.model.Genre;
import filmorate.storage.genre.GenreDbStorage;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Collection<Genre> getAll() {
        return genreDbStorage.getAll();
    }

    public Genre getById(int id) {
        Genre genre = genreDbStorage.getById(id);
        if (genre == null) {
            throw new NoSuchElementException("Жанр с id " + id + " не найден");
        }
        return genre;
    }
}
