package filmorate.storage.genre;

import filmorate.model.Genre;
import java.util.Collection;

public interface GenreStorage {
    Genre getById(int id);

    Collection<Genre> getAll();
}

