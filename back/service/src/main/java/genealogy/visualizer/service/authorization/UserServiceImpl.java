package genealogy.visualizer.service.authorization;

import genealogy.visualizer.api.model.User;
import genealogy.visualizer.model.exception.ForbiddenException;
import genealogy.visualizer.service.UserDAO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByLogin(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("Пользователь с логином: %s не найден", username));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    @Override
    public User findByLogin(String login) {
        return Optional.of(userDAO.findByLogin(login))
                .map(user -> new User(user.getLogin(), user.getPassword()))
                .orElse(null);
    }

    @Override
    public void createUser(User user) {
        if (userDAO.findByLogin(user.getLogin()) != null) {
            throw new ForbiddenException(String.format("Пользователь с логином: %s уже существует", user.getLogin()));
        }
        userDAO.save(new genealogy.visualizer.entity.User(user.getLogin(), passwordEncoder.encode(user.getPassword())));
    }
}
