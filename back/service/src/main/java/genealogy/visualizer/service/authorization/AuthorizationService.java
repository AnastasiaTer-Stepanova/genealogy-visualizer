package genealogy.visualizer.service.authorization;

import genealogy.visualizer.api.model.RegistrationInfo;
import genealogy.visualizer.api.model.User;

public interface AuthorizationService {

    String authorization(User user);

    void registration(RegistrationInfo registrationInfo);
}
