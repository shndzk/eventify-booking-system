package app.event.service;


import event.app.dto.AuthResponse;
import event.app.dto.LoginRequest;
import event.app.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest req);
    AuthResponse login(LoginRequest req);
}

