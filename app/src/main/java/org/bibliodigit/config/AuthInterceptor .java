package org.bibliodigit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bibliodigit.domain.User;
import org.bibliodigit.domain.port.UserService;
import org.bibliodigit.security.RequireRole;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        
        if (requireRole == null) {
            return true;
        }

        String token = request.getHeader("Authorization");
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Optional<User> userOpt = userService.validateToken(token);

        if (userOpt.isEmpty()) {
            log.warn("Invalid or missing token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized - Invalid or missing token\"}");
            return false;
        }

        User user = userOpt.get();
        String userRole = user.getTypeUser().getType();

        String[] allowedRoles = requireRole.value();
        boolean hasPermission = Arrays.asList(allowedRoles).contains(userRole);

        if (!hasPermission) {
            log.warn("User {} with role {} tried to access protected resource requiring roles: {}", 
                user.getEmail(), userRole, Arrays.toString(allowedRoles));
            
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(
                String.format("{\"error\": \"Forbidden - Requires role: %s\"}", Arrays.toString(allowedRoles))
            );
            return false;
        }

        request.setAttribute("currentUser", user);
        request.setAttribute("currentUserId", user.getId());
        request.setAttribute("currentUserRole", userRole);
        
        log.debug("Authenticated user: {} with role: {}", user.getEmail(), userRole);
        return true;
    }
}
