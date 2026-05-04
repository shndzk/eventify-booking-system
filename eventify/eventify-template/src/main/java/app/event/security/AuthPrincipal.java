package app.event.security;


public record AuthPrincipal(Long userId, String email, String role) {
    public boolean isAdmin() { return "ADMIN".equals(role); }
}

