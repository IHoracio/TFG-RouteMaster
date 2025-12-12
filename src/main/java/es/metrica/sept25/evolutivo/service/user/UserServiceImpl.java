package es.metrica.sept25.evolutivo.service.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User save(User user) {

		if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		return userRepository.save(user);
	}

	@Override
	public Optional<User> getByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<User> getAll() {
		return userRepository.findAll();
	}

	@Override
	@Transactional
	public void deleteByEmail(String email) {
		Optional<User> user = getByEmail(email);
		if (user.isPresent()) {
			userRepository.deleteByEmail(email);
		}
	}
	@Override
	@Transactional
	public User createUser(String nombre, String apellido, String password, String email) {

	    if (userRepository.findByEmail(email).isPresent()) {
	        throw new RuntimeException("El usuario ya existe");
	    }

	    User user = new User();
	    user.setName(nombre);
	    user.setSurname(apellido);
	    user.setPassword(password);
	    user.setEmail(email);

	    return save(user);
	}
}
