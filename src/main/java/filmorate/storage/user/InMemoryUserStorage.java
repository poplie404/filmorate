package filmorate.storage.user;


import filmorate.model.User;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NoSuchElementException("Пользователь с id " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(int id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("Пользователь с id " + id + " не найден");
        }
        users.remove(id);
    }

    @Override
    public User getById(int id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("Пользователь с id " + id + " не найден");
        }
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    private int getNextId() {
        return users.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1;
    }
}


