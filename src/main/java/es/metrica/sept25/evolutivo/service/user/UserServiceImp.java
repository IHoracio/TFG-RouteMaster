package es.metrica.sept25.evolutivo.service.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.repository.UserRepository;

@Service
public class UserServiceImp implements UserService{

	@Autowired
    private UserRepository repository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User save(User user) {
    	
    	if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return repository.save(user);
    }

    @Override
    public Optional<User> getByMail(String mail) {
        return repository.findById(mail);
    }

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public void delete(String mail) {
        repository.deleteById(mail);
    }
}
