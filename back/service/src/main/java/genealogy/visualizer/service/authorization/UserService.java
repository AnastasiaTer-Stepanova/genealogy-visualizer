package genealogy.visualizer.service.authorization;

import genealogy.visualizer.api.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User findByLogin(String login);

    void createUser(User user);

}
