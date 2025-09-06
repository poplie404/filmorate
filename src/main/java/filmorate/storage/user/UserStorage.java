package filmorate.storage.user;

import filmorate.model.User;

import java.util.Collection;

interface UserStorage {
    User add(User user);

    User update(User user);

    void delete(int id);

    User getById(int id);

    Collection<User> getAll();
}
