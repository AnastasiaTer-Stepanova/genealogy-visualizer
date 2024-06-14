package genealogy.visualizer.controller;

import genealogy.visualizer.api.AuthorizationApi;
import genealogy.visualizer.api.model.RegistrationInfo;
import genealogy.visualizer.api.model.User;
import genealogy.visualizer.service.authorization.AuthorizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationController implements AuthorizationApi {

    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public ResponseEntity<String> authorization(User user) {
        return ResponseEntity.ok(authorizationService.authorization(user));
    }

    @Override
    public ResponseEntity<Void> registration(RegistrationInfo registrationInfo) {
        authorizationService.registration(registrationInfo);
        return ResponseEntity.ok().build();
    }
}
