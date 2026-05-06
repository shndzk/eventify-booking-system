package app.event.security;


import app.event.exceptions.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    public AuthPrincipal current() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || !(a.getPrincipal() instanceof AuthPrincipal p)) {
            throw new UnauthorizedException("Не авторизован");
        }
        return p;
    }
    public Long currentUserId() { return current().userId(); }
    public boolean isAdmin() { return current().isAdmin(); }
}

