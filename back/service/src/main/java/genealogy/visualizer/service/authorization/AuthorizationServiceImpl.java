package genealogy.visualizer.service.authorization;

import genealogy.visualizer.api.model.RegistrationInfo;
import genealogy.visualizer.api.model.User;
import genealogy.visualizer.model.exception.ForbiddenException;
import genealogy.visualizer.model.exception.UnauthorizedException;
import genealogy.visualizer.service.ParamDAO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AuthorizationServiceImpl implements AuthorizationService {

    private final ParamDAO paramDAO;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthorizationServiceImpl(ParamDAO paramDAO, UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.paramDAO = paramDAO;
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    private static final String REGISTRATION_ENABLE_PARAM_NAME = "registration_enable";

    @Override
    public String authorization(User user) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword()));
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            throw new ForbiddenException();
        }
        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(user.getLogin());
        } catch (UsernameNotFoundException e) {
            throw new UnauthorizedException("Доступ запрещен. Пожалуйста, зарегистрируйтесь.");
        }
        return jwtService.generateToken(userDetails);
    }

    @Override
    public void registration(RegistrationInfo registrationInfo) {
        if (paramDAO.getBooleanParamOrDefault(REGISTRATION_ENABLE_PARAM_NAME, Boolean.FALSE)) {
            throw new ForbiddenException("Регистрация в данный момент недоступна.");
        }
        userService.createUser(new User(registrationInfo.getLogin(), registrationInfo.getPassword()));
    }
}
