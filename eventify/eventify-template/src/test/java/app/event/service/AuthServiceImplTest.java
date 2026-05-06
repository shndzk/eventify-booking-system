package app.event.service;


import event.app.dto.AuthResponse;
import event.app.dto.LoginRequest;
import event.app.dto.RegisterRequest;
import app.event.entity.Role;
import app.event.entity.UserEntity;
import app.event.exceptions.BadRequestException;
import app.event.exceptions.UnauthorizedException;
import app.event.repository.NotificationPreferencesRepository;
import app.event.repository.UserRepository;
import app.event.security.JwtService;
import app.event.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock UserRepository userRepo;
    @Mock NotificationPreferencesRepository notifRepo;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @InjectMocks AuthServiceImpl service;

    @Test
    void register_shouldFail_whenEmailExists() {
        when(userRepo.existsByEmail("a@b.c")).thenReturn(true);
        assertThatThrownBy(() -> service.register(new RegisterRequest("a@b.c","password1")))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void login_shouldFail_whenPasswordWrong() {
        UserEntity u = UserEntity.builder().id(1L).email("a@b.c").passwordHash("h").role(Role.USER).build();
        when(userRepo.findByEmail("a@b.c")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("pw","h")).thenReturn(false);
        assertThatThrownBy(() -> service.login(new LoginRequest("a@b.c","pw")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void login_shouldReturnToken_whenSuccess() {
        UserEntity u = UserEntity.builder().id(1L).email("a@b.c").passwordHash("h").role(Role.USER).build();
        when(userRepo.findByEmail("a@b.c")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("pw","h")).thenReturn(true);
        when(jwtService.generate(1L,"a@b.c",Role.USER)).thenReturn("T");
        AuthResponse r = service.login(new LoginRequest("a@b.c","pw"));
        assertThat(r.getToken()).isEqualTo("T");
        assertThat(r.getRole()).isEqualTo(AuthResponse.RoleEnum.USER);
    }
}

