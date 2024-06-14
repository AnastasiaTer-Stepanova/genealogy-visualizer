package genealogy.visualizer.service.authorization;

import genealogy.visualizer.api.model.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    String generateToken(UserDetails userDetails);

    boolean isTokenValid(String token, User user);

    String getLogin(String token);

}
