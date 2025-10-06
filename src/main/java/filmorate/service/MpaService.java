package filmorate.service;

import filmorate.model.Mpa;
import filmorate.storage.mpa.MpaDbStorage;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Collection<Mpa> getAll() {
        return mpaDbStorage.getAll();
    }

    public Mpa getById(int id) {
        Mpa mpa = mpaDbStorage.getById(id);
        if (mpa == null) {
            throw new NoSuchElementException("Рейтинг MPA с id " + id + " не найден");
        }
        return mpa;
    }
}
