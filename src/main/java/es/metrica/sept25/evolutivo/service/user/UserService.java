package es.metrica.sept25.evolutivo.service.user;

import java.util.List;
import java.util.Optional;

import es.metrica.sept25.evolutivo.entity.user.User;

public interface UserService {
    User save(User user);

    Optional<User> getByEmail(String mail);

    List<User> getAll();

    void deleteByEmail(String mail);

	User createUser(String name, String surname, String password, String email);
}
