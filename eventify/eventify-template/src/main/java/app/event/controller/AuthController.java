package app.event.controller;


import event.app.api.AuthApi;
import event.app.dto.AuthResponse;
import event.app.dto.LoginRequest;
import event.app.dto.RegisterRequest;
import app.event.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthResponse> authLoginPost(LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @Override
    public ResponseEntity<AuthResponse> authRegisterPost(RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }
}


