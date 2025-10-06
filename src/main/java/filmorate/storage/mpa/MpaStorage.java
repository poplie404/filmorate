package filmorate.storage.mpa;

import filmorate.model.Mpa;

import java.util.Collection;

public interface MpaStorage {
    Mpa getById(int id);

    Collection<Mpa> getAll();
}
