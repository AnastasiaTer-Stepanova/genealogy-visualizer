package genealogy.visualizer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import genealogy.visualizer.api.model.ErrorResponse;
import genealogy.visualizer.api.model.User;
import genealogy.visualizer.service.authorization.JwtService;
import genealogy.visualizer.service.authorization.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public JwtRequestFilter(JwtService jwtService, UserService userService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            if (!HttpMethod.GET.name().equals(request.getMethod()) &&
                    !request.getRequestURI().startsWith("/authorization") &&
                    !request.getRequestURI().startsWith("/registration") &&
                    !request.getRequestURI().startsWith("/swagger-ui/") &&
                    !request.getRequestURI().startsWith("/swagger-resources/") &&
                    !request.getRequestURI().startsWith("/v3/api-docs/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON.toString());
                response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
                ErrorResponse errorResponse = new ErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, "Доступ запрещен. Пожалуйста, авторизуйтесь.");
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                response.getWriter().flush();
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(BEARER_PREFIX.length());
        String login = jwtService.getLogin(jwt);

        if (StringUtils.isNotEmpty(login) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(login);

            if (jwtService.isTokenValid(jwt, new User(userDetails.getUsername(), userDetails.getPassword()))) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}
