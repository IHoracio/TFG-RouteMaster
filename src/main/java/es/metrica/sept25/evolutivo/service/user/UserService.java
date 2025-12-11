package es.metrica.sept25.evolutivo.service.user;

import java.util.List;
import java.util.Optional;

import es.metrica.sept25.evolutivo.entity.user.User;

public interface UserService {


    User save(User user);

    Optional<User> getByMail(String mail);

    List<User> getAll();

    void delete(String mail);
}
